package kiit.apis

import java.io.File
import kiit.apis.core.*
import kiit.apis.meta.MetaBuilder
import kiit.apis.meta.MetaDecoder
import kiit.apis.meta.MetaHandler
import kiit.apis.routes.*
import kiit.apis.rules.AuthRule
import kiit.apis.rules.ParamsRule
import kiit.apis.rules.ProtoRule
import kiit.apis.rules.RouteRule
import kiit.apis.services.*
import kiit.apis.setup.HostAware
import kiit.common.*
import kiit.common.crypto.Encryptor
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
import kiit.serialization.deserializer.json.JsonDeserializer
import org.json.simple.JSONObject
import kotlin.reflect.KClass

/**
 * This is the core container hosting, managing and executing the source independent apis.
 * @param ctx : Context of the environment @see[slatekti.common.Context]
 * @param apis : APIs to host/serve
 * @param middleware : Hooks and middleware for filters, conversions, execution
 * @param settings : Settings for the server
 */
open class ApiServer(
    val ctx: Context,
    val routes: List<VersionAreas>,
    val rewriter: Rewriter? = null,
    val namedMiddlewares: List<Pair<String,Middleware>> = listOf(),
    val auth: Auth? = null,
    val decoder: ((Request, Encryptor?) -> Deserializer<JSONObject>)? = null,
    metas : List<Pair<KClass<*>, MetaHandler>> = listOf(),
    val settings: Settings = Settings()
)  {

    /**
     * Load all the routes from the APIs supplied.
     * The API setup can be either annotation based or public methods on the Class
     */
    val router = Router(routes, settings.naming)


    /**
     * Builds/Deserializes types from the Request and its metadata
     */
    val metas : MetaBuilder = MetaBuilder(MetaDecoder.of(metas))

    /**
     * The help class to handle help on an area, api, or action
     */
    val help: Help get() = Help(this, router, settings.docKey) { settings.docGen() }

    /**
     * Logger for this server
     */
    private val logger: Logger = ctx.logs.getLogger("api")


    private val middlewares = namedMiddlewares.map { it.second }


    /**
     * Initialize all the routes with reference to this server
     */
    init {
        router.visitApis { _, api ->
            if(api.items.isNotEmpty()) {
                val action = api.items[0]
                if(action.handler is MethodExecutor) {
                    val executor = action.handler as MethodExecutor
                    setApiHost(executor.call.instance, this)
                }
            }
        }
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
    fun get(req: Request): RouteMapping? {
        val gblVersion = req.version
        val apiVersion = req.meta.getStringOrNull("x-api-version")
        return get(req.verb, req.area, req.name, req.action, gblVersion, apiVersion)
    }


    /**
     * gets the @see[Target] ( mapped method ) associated with the route info ( area.api.action )
     * @param area   : e.g. "accounts"
     * @param name   : e.g. "signup"
     * @param action : e.g. "register"
     * @return
     */
    fun get(verb:String, area: String, name: String, action: String, globalVersion:String = ApiConstants.versionZero, version:String? = null): RouteMapping? {
        val action = router.action(verb, area, name, action, globalVersion, version)
        return action
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
        val request = rewriter?.process(initial) ?: initial

        // Route    : area.api.action
        val routeResult = RouteRule.isValid(request)
        if(!routeResult) return Outcomes.invalid("Route ${request.request.path} invalid")

        // Target
        val targetResult = get(request.request.verb, request.request.area, request.request.name, request.request.action)
        if(targetResult == null) return Outcomes.invalid("Unable to find action")
        val req = request.copy(target = targetResult)

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
                else -> {
                    val handler = instance.handler
                    if(handler is MethodExecutor) {
                        val executor = handler
                        if(executor.call.instance is Middleware) {
                            executeWithMiddleware(req, executor.call.instance)
                        }
                        else {
                            executeWithMiddleware(req, null)
                        }
                    } else if(middlewares.isNotEmpty()) {
                        Middleware.process(req, 0, middlewares) {
                            executeMethod(req)
                        }
                    } else {
                        executeWithMiddleware(req, null)
                    }
                }
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
            null -> executeMethod( req)
            else  -> {
                middleware.process(req) {
                    executeMethod(req)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected open suspend fun executeMethod(request: ApiRequest): Outcome<ApiResult> {
        // Finally make call.
        val target = request.target
        val converter = decoder?.invoke(request.request, ctx.enc) ?: JsonDeserializer(request.request, ctx.enc)
        val executor = target!!.handler as MethodExecutor
        val call = executor.call
        val inputs = Calls.fillArgs(converter, target, call, request.request)
        val returnVal = Calls.callMethod(call.klass, call.instance, call.member.name, inputs)
        val wrapped = returnVal?.let { res ->
            if (res is Result<*, *>) {
                (res as Result<ApiResult, Err>)
            } else {
                Outcomes.of(res!!)
            }
        } ?: Outcomes.of(Exception("Received null"))
        return wrapped
    }


    private fun handleError(req: Request, ex: Exception) {
        val parts = req.structured()
        val info = parts.joinToString { "${it.first}=${it.second?.toString()}" }
        logger.error("API Server: ERROR: $info", ex)
    }


    companion object {

        @JvmStatic
        fun of(ctx: Context, routes: List<VersionAreas>, auth: Auth? = null, source: Source? = null): ApiServer {
            val server = ApiServer(ctx, routes, null, listOf(), auth, null, listOf(), Settings(source ?: Source.API))
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
            val sample = if (action != null ) {
//                val parameters = when (action) {
//                    is Success -> action.value.action.paramsUser
//                    is Failure -> listOf()
//                }
//                val serializer = Serialization.sampler() as SerializerSample
//                val text = serializer.serializeParams(parameters)
//                text
                 "NOT implemented"
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
