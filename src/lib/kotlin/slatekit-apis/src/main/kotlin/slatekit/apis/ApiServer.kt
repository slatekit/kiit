package slatekit.apis

import java.io.File
import kotlin.reflect.KClass
import slatekit.apis.core.*
import slatekit.apis.core.Target
import slatekit.apis.routes.Routes
import slatekit.apis.rules.AuthRule
import slatekit.apis.rules.ParamsRule
import slatekit.apis.rules.ProtoRule
import slatekit.apis.rules.RouteRule
import slatekit.apis.services.*
import slatekit.apis.setup.HostAware
import slatekit.apis.setup.loadAll
import slatekit.common.*
import slatekit.common.ext.numbered
import slatekit.common.ext.structured
import slatekit.common.ext.toResponse
import slatekit.common.log.Logger
import slatekit.common.requests.CommonRequest
import slatekit.common.requests.Request
import slatekit.common.requests.Response
import slatekit.context.Context
import slatekit.meta.*
import slatekit.meta.deserializer.Deserializer
import slatekit.policy.Process
import slatekit.results.*
import slatekit.results.builders.Outcomes
import kotlin.reflect.KCallable

/**
 * This is the core container hosting, managing and executing the source independent apis.
 * @param ctx : Context of the environment @see[slatekti.common.Context]
 * @param apis : APIs to host/serve
 * @param hooks : Hooks and middleware for filters, conversions, execution
 * @param settings : Settings for the server
 */
open class ApiServer(
    val ctx: Context,
    val apis: List<slatekit.apis.routes.Api>,
    val hooks: List<Middleware> = listOf(),
    val auth: Auth? = null,
    val settings: Settings = Settings()
)  {

    /**
     * Load all the routes from the APIs supplied.
     * The API setup can be either annotation based or public methods on the Class
     */
    val routes = Routes(loadAll(apis, settings.naming), settings.naming)

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
    suspend fun executeResponse(req: Request): Response<Any> {
        return executeAttempt(req, null).toResponse()
    }


    /**
     * Call with inputs instead of the request
     */
    suspend fun executeAttempt(area: String, api: String, action: String, verb: Verb, opts: Map<String, Any>, args: Map<String, Any>): Try<Any> {
        val req = CommonRequest.cli(area, api, action, verb.name, opts, args)
        return executeAttempt(req, null)
    }

    /**
     * calls the api/action associated with the request with optional execution options.
     * This is to allow requests to be sourced from some other source such as a secure storage
     * @param req
     * @return
     */
    suspend fun executeAttempt(req: Request, options: Flags?): Try<Any> {
        val result = executeOutcome(req, options)
        return result.toTry()
    }

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun executeOutcome(req: Request, options: Flags?): Outcome<Any> {
        val result = try {
            val result = execute(req, options)
            record(req, result, logger)
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
    suspend fun execute(raw: Request, options: Flags? = null): Outcome<Any> {
        // Help ?
        val helpCheck = help.process(raw)
        if (helpCheck.success) return helpCheck

        // Build ApiRequest from the raw request ( this is used for middleware )
        val request = ApiRequest(this, auth, ctx, raw, null, raw.source, null)

        // Route    : area.api.action
        val routeResult = RouteRule.validate(request)
        if(!routeResult.success) return routeResult

        // Target
        val targetResult = request.host.get(raw.area, raw.name, raw.action)
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
        val result = try {
            executeMethod(Ctx.of(this, this.ctx, req), req)
        } catch(ex:Exception){
            when(ex){
                is ExceptionErr -> Outcomes.unexpected(ex.err, Codes.UNEXPECTED)
                else            -> Outcomes.unexpected<ApiResult>(ex)
            }
        }
        return result
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
        fun of(ctx: Context, apis: List<slatekit.apis.routes.Api>, auth: Auth? = null, source: Source? = null): ApiServer {
            val server = ApiServer(ctx, apis, listOf(), auth, Settings(source ?: Source.Web))
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


        fun record(req: Request, res:Outcome<Any>, logger:Logger){
            logger.info({
                val info = listOf("path" to req.path) + res.structured()
                val summary= info.joinToString { "${it.first}=${it.second?.toString()}" }
                val inputs = req.structured().joinToString { "${it.first}=${it.second?.toString()}" }
                "API Server Result: $summary, inputs : $inputs"
            }, null)

            res.onFailure {
                val numbered = it.numbered().joinToString(newline)
                logger.error("API Server Error(s): $newline$numbered")
            }
        }
    }
}
