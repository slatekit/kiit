package kiit.apis

import java.io.File
import kotlin.reflect.KClass
import kiit.apis.core.*
import kiit.apis.core.Target
import kiit.apis.routes.Routes
import kiit.apis.rules.AuthRule
import kiit.apis.rules.ParamsRule
import kiit.apis.rules.ProtoRule
import kiit.apis.rules.RouteRule
import kiit.apis.services.*
import kiit.apis.setup.HostAware
import kiit.apis.setup.loadAll
import kiit.common.*
import kiit.common.ext.numbered
import kiit.common.ext.structured
import kiit.requests.toResponse
import kiit.common.log.Logger
import kiit.requests.CommonRequest
import kiit.requests.Request
import kiit.requests.Response
import kiit.context.Context
import kiit.meta.*
import kiit.serialization.deserializer.Deserializer
import kiit.results.*
import kiit.results.builders.Outcomes
import kiit.serialization.Serialization
import kiit.serialization.SerializerSample
import kotlin.reflect.KCallable

/**
 * This is the core container hosting, managing and executing the source independent apis.
 * @param ctx : Context of the environment @see[slatekti.common.Context]
 * @param apis : APIs to host/serve
 * @param middleware : Hooks and middleware for filters, conversions, execution
 * @param settings : Settings for the server
 */
open class ApiServer(
    val ctx: Context,
    val apis: List<kiit.apis.routes.Api>,
    val writer: Rewriter? = null,
    val middleware: Middleware? = null,
    val auth: Auth? = null,
    val settings: Settings = Settings()
)  {

    /**
     * Load all the routes from the APIs supplied.
     * The API setup can be either annotation based or public methods on the Class
     */
    val routes = Routes(loadAll(apis, settings.source, settings.naming), settings.naming)

    /**
     * The help class to handle help on an area, api, or action
     */
    val help: Help get() = Help(this, routes, settings.docKey) { settings.docGen() }

    /**
     * Logger for this server
     */
    private val logger: Logger = ctx.logs.getLogger("api")


    /**
     * Initialize all the routes with reference to this server
     */
    init {
        routes.visitApis { _, api -> setApiHost(api.singleton, this) }
    }


    /**
     * Generates a sample response for the route specified in the request ( area.api.action )
     * This checks the methods ( associated with the route ) and generates a response based on
     * its inputs( parameter types ) and its return type.
     */
    fun sample(req: Request, path: File): Notice<String> {
        return sample(this, req, path)
    }


    /**
     * gets the @see[Target] ( mapped method ) associated with the route ( area.api.action ) in the Request
     * @param req : Request to get route info from
     * @return
     */
    fun get(req: Request): Outcome<Target> {
        return get(req.area, req.name, req.action)
    }


    /**
     * gets the @see[Target] ( mapped method ) associated with the route info ( area.api.action )
     * @param area   : e.g. "accounts"
     * @param name   : e.g. "signup"
     * @param action : e.g. "register"
     * @return
     */
    fun get(area: String, name: String, action: String): Outcome<Target> {
        return routes.api(area, name, action, ctx)
    }


    /**
     * gets the @see[Target] ( mapped method ) associated with annotations on the class/method supplied
     * @return
     */
    fun get(clsType: KClass<*>, member: KCallable<*>): Outcome<Target> {
        val apiAnno = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)
        return apiAnno?.let { anno ->
            val action = when(val actionAnno = Reflector.getAnnotationForMember<Action>(member, Action::class)){
                null -> member.name
                else -> if (actionAnno.name.isBlank()) member.name else actionAnno.name
            }
            get(anno.area, anno.name, action)
        } ?: Outcomes.errored("member/annotation not found for : ${member.name}")
    }


    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun executeResponse(req: Request): Response<ApiResult> {
        return executeAttempt(req).toResponse()
    }


    /**
     * Call with inputs instead of the request
     */
    suspend fun executeAttempt(area: String, api: String, action: String, verb: Verb, opts: Map<String, Any>, args: Map<String, Any>): Try<ApiResult> {
        val req = CommonRequest.api(area, api, action, verb.name, opts, args)
        return executeAttempt(req)
    }

    /**
     * calls the api/action associated with the request with optional execution options.
     * This is to allow requests to be sourced from some other source such as a secure storage
     * @param req
     * @return
     */
    suspend fun executeAttempt(req: Request): Try<ApiResult> {
        val result = executeOutcome(req)
        return result.toTry()
    }

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun executeOutcome(req: Request): Outcome<ApiResult> {
        val result = try {
            val result = execute(req)
            if(settings.record) {
                record(req, result, logger)
            }
            result
        } catch (ex: Exception) {
            handleError(req, ex)
            val err = when(ex) {
                is ExceptionErr -> ex.err
                else -> Err.ex(ex)
            }
            Outcomes.errored<Any>(err)
        }
        return result
    }

    /**
     * Executes the api request after performing basic checks/rules
     * @param cmd
     * @return
     */
    suspend fun execute(raw: Request): Outcome<ApiResult> {
        // Help ?
        val helpCheck = help.process(raw)
        if (helpCheck.success) return helpCheck

        // Build ApiRequest from the raw request ( this is used for middleware )
        val initial = ApiRequest(this, auth, ctx, raw, null, raw.source, null)
        val request = writer?.process(initial) ?: initial

        // Route    : area.api.action
        val routeResult = RouteRule.isValid(request)
        if(!routeResult) return Outcomes.invalid("Route ${request.request.path} invalid")

        // Target
        val targetResult = request.host.get(request.request.area, request.request.name, request.request.action)
        if(!targetResult.success) return targetResult
        val req = request.copy(target = targetResult.getOrNull())

        // Protocol : e.g. cli, web, que
        val protocolResult = ProtoRule.validate(req)
        if(!protocolResult.success) return protocolResult

        // Auth     : e.g. cli, web, que
        val authResult = AuthRule.validate(req)
        if(!authResult.success) return authResult

        // Params   : e.g. cli, web, que
        val paramsResult = ParamsRule.validate(req)
        if(!paramsResult.success) return paramsResult

        // Step 5: Execute request
        val result:Outcome<ApiResult> = try {
            val instance = req.target
            when {
                instance == null  -> Outcomes.errored("Route not mapped")
                instance.instance is Middleware -> executeWithMiddleware(req, instance.instance)
                middleware != null -> executeWithMiddleware(req, middleware)
                else -> executeWithMiddleware(req, null)
            }

        } catch(ex:Exception){
            when(ex){
                is ExceptionErr -> Outcomes.unexpected(ex.err, Codes.UNEXPECTED)
                else            -> Outcomes.unexpected<ApiResult>(ex)
            }
        }
        return result
    }


    private suspend fun executeWithMiddleware(req:ApiRequest, middleware: Middleware?): Outcome<ApiResult> {
        return when(middleware) {
            null -> executeMethod(Ctx.of(this, this.ctx, req), req)
            else  -> {
                middleware.process(req) {
                    executeMethod(Ctx.of(this, this.ctx, req), req)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected open suspend fun executeMethod(context: Ctx, request: ApiRequest): Outcome<ApiResult> {
        // Finally make call.
        val req = context.req
        val target = context.target
        val converter = settings.decoder?.invoke(req, ctx.enc) ?: Deserializer(req, ctx.enc)
        val inputs = fillArgs(converter, target, req)
        val returnVal = Calls.callMethod(target.api.klass, target.instance, target.action.member.name, inputs)
        val wrapped = returnVal?.let { res ->
            if (res is Result<*, *>) {
                (res as Result<ApiResult, Err>)
            } else {
                Outcomes.of(res!!)
            }
        } ?: Outcomes.of(Exception("Received null"))
        return wrapped
    }


    private val typeDefaults = mapOf(
        "String" to "",
        "Boolean" to false,
        "Int" to 0,
        "Long" to 0L,
        "Double" to 0.0,
        "DateTime" to DateTime.now()
    )

    private fun fillArgs(deserializer: Deserializer, apiRef: Target, cmd: Request): Array<Any?> {
        val action = apiRef.action
        // Check 1: No args ?
        return if (!action.hasArgs)
            arrayOf()
        // Check 2: 1 param with default and no args
        else if (action.isSingleDefaultedArg() && cmd.data.size() == 0) {
            val argType = action.paramsUser[0].type.toString()
            val defaultVal = if (typeDefaults.contains(argType)) typeDefaults[argType] else null
            arrayOf<Any?>(defaultVal ?: "")
        } else {
            deserializer.deserialize(action.params)
        }
    }

    private suspend fun handleError(req: Request, ex: Exception) {
        val parts = req.structured()
        val info = parts.joinToString { "${it.first}=${it.second?.toString()}" }
        logger.error("API Server: ERROR: $info", ex)
    }


    companion object {

        @JvmStatic
        fun of(ctx: Context, apis: List<kiit.apis.routes.Api>, auth: Auth? = null, source: Source? = null): ApiServer {
            val server = ApiServer(ctx, apis, null, null, auth, Settings(source ?: Source.API))
            return server
        }

        @JvmStatic
        fun setApiHost(item: Any?, host: ApiServer) {
            if (item is HostAware) {
                item.setApiHost(host)
            }
        }

        /**
         * Generates a sample response based on the inputs/outputs
         */
        fun sample(server:ApiServer, req: Request, path: File): Notice<String> {
            val action = server.get(req)
            val sample = if (action.success) {
                val parameters = when (action) {
                    is Success -> action.value.action.paramsUser
                    is Failure -> listOf()
                }
                val serializer = Serialization.sampler() as SerializerSample
                val text = serializer.serializeParams(parameters)
                text
            } else "Unable to find command: " + req.path

            path.writeText(sample)
            return Success("sample call written to : ${path.absolutePath}")
        }


        fun record(req: Request, res:Outcome<ApiResult>, logger:Logger){
            logger.info {
                val info = listOf("path" to req.path) + res.structured()
                val summary= info.joinToString { "${it.first}=${it.second?.toString()}" }
                val inputs = req.structured().joinToString { "${it.first}=${it.second?.toString()}" }
                "API Server Result: $summary, inputs : $inputs"
            }

            res.onFailure {
                val numbered = it.numbered().joinToString(newline)
                logger.error("API Server Error(s): $newline$numbered")
            }
        }
    }
}
