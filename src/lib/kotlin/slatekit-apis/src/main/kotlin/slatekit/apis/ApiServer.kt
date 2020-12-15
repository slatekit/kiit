package slatekit.apis

import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import slatekit.apis.core.*
import slatekit.apis.core.Target
import slatekit.apis.routes.Routes
import slatekit.apis.services.*
import slatekit.apis.setup.HostAware
import slatekit.apis.setup.loadAll
import slatekit.apis.support.ExecSupport
import slatekit.common.*
import slatekit.common.ext.numbered
import slatekit.common.ext.structured
import slatekit.common.log.Logger
import slatekit.common.requests.Request
import slatekit.context.Context
import slatekit.meta.*
import slatekit.meta.deserializer.Deserializer
import slatekit.policy.Process
import slatekit.results.*
import slatekit.results.builders.Outcomes

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
    val settings: ApiSettings = ApiSettings()
) : ExecSupport {

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
     * Provides access to naming conventions used for actions
     */
    fun rename(text: String): String = settings.naming?.rename(text) ?: text

    fun setApiContainerHost() {
        routes.visitApis { _, api -> ApiServer.setApiHost(api.singleton, this) }
    }

    override fun host(): ApiServer = this

    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun get(cmd: Request): Outcome<Target> {
        return get(cmd.area, cmd.name, cmd.action)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun get(area: String, name: String, action: String): Outcome<Target> {
        return routes.api(area, name, action, ctx)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun get(clsType: KClass<*>, member: KCallable<*>): Outcome<Target> {
        val apiAnno = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)
        val result = apiAnno?.let { anno ->

            val area = anno.area
            val api = anno.name
            val actionAnno = Reflector.getAnnotationForMember<Action>(member, Action::class)
            val action = actionAnno?.let { act ->
                val action = if (act.name.isBlank()) member.name else act.name
                action
            } ?: member.name
            val info = get(area, api, action)
            info
        } ?: Outcomes.errored("member/annotation not found for : ${member.name}")
        return result
    }

    fun sample(cmd: Request, path: File): Notice<String> {
        val action = get(cmd)
        val sample = if (action.success) {
            val parameters = when (action) {
                is Success -> action.value.action.paramsUser
                is Failure -> listOf()
            }
            val serializer = Serialization.sampler() as SerializerSample
            val text = serializer.serializeParams(parameters)
            text
        } else "Unable to find command: " + cmd.path

        path.writeText(sample)
        return Success("sample call written to : ${path.absolutePath}")
    }

    /**
     * calls the api/action associated with the request with optional execution options.
     * This is to allow requests to be sourced from some other source such as a secure storage
     * @param req
     * @return
     */
    suspend fun call(req: Request, options: Flags?): Try<Any> {
        val result = try {
            val result = execute(req, options)
            record(req, result)
            result
        } catch (ex: Exception) {
            handleError(req, ex)
            val err = when(ex) {
                is ExceptionErr -> ex.err
                else -> Err.ex(ex)
            }
            Outcomes.errored<Any>(err)
        }
        return result.toTry()
    }

    /**
     * Executes the api request in a pipe-line of various checks and validations.
     * NOTE: This is effectively the core processing method of API Container.
     * The flow is as follows:
     *
     * 1. before:
     *      - BEFORE : formatters   : pre-request formatters
     *      - BEFORE : pre-process  : built in validators
     *      - BEFORE : before hooks : API overrides
     *      - BEFORE : inputters    : pre-request processors
     *      - EXECUTE:
     *      - AFTER  : outputters
     *      - AFTER  : after hooks  : API overrides
     *      - AFTER  : outputters   : post process
     * @param cmd
     * @return
     */
    suspend fun execute(raw: Request, options: Flags? = null): Outcome<Any> {
        // Step 1: Check for help / discovery
        val helpCheck = help.process(raw)
        if (helpCheck.success) {
            return helpCheck
        }

        // Step 2: Build ApiRequest from the raw request ( this is used for middleware )
        val rawRequest = ApiRequest(this, ctx, raw, null, raw.source, null)

        // Step 3: Hooks: Pre-Processing Stage 1 : rewrite request, and ensure system validations
        val startInput = Outcomes.success(rawRequest)
        val validated = startInput

        // Step 4: Hooks: Pre-Processing Stage 2: run through more hooks ( API level & supplied )
        val requested = validated

        // Step 5: Execute request
        val executed = try {
            requested.flatMap { request ->
                request.target?.let {
                    if (it.instance is Process<*, *>) {
                        val processor = it.instance as Process<ApiRequest, ApiResult>
                        processor.process(request) {
                            executeMethod(Ctx.of(this, this.ctx, request), request)
                        }
                    } else {
                        executeMethod(Ctx.of(this, this.ctx, request), request)
                    }
                } ?: executeMethod(Ctx.of(this, this.ctx, request), request)

            }
        } catch(ex:Exception){
            when(ex){
                is ExceptionErr -> Outcomes.unexpected(ex.err, Codes.UNEXPECTED)
                else            -> Outcomes.unexpected<ApiResult>(ex)
            }
        }

        // Step 7: Hooks: Post-Processing Stage 2: remaining hooks ( afters, built-ins, outputters )
        val result = executed
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


    private fun record(req: Request, res:Outcome<Any>){
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


    companion object {

        @JvmStatic
        fun of(ctx: Context, apis: List<slatekit.apis.routes.Api>, auth: Auth? = null, source: Source? = null): ApiServer {
            val server = ApiServer(ctx, apis, listOf(), ApiSettings(source ?: Source.Web))
            return server
        }

        @JvmStatic
        fun setApiHost(item: Any?, host: ApiServer) {
            if (item is HostAware) {
                item.setApiHost(host)
            }
        }

        /**
         * Default list of API Request input validators
         */
        @JvmStatic
        fun middleware(): List<Middleware> {
            return listOf(
                Routing(),
                Targets(),
                Protos(),
                Validate()
            )
        }
    }
}
