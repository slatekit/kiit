package slatekit.apis

import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import slatekit.apis.core.*
import slatekit.apis.core.Target
import slatekit.apis.hooks.*
import slatekit.apis.setup.HostAware
import slatekit.apis.setup.loadAll
import slatekit.apis.support.ExecSupport
import slatekit.common.*
import slatekit.common.log.Logger
import slatekit.common.requests.Request
import slatekit.functions.Input
import slatekit.functions.Output
import slatekit.functions.Processor
import slatekit.meta.*
import slatekit.results.*
import slatekit.results.Status
import slatekit.results.builders.Notices
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
    val apis: List<slatekit.apis.core.Api>,
    val hooks: ApiHooks = ApiHooks(),
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
     * Helps run the hooks/middleware
     */
    private val processor = Processor<ApiRequest, ApiResult>()

    /**
     * Request pre-processors ( filters, converters )
     */
    private val preProcessBuiltIns: List<Input<ApiRequest>> = defaultPreHooks()
    private val preProcessAPIHooks = listOf(Befores())


    /**
     * Request pre-processors ( filters, converters )
     */
    private val postProcessAPIHooks: List<Output<ApiRequest, ApiResult>> = listOf(Afters())
    private val postProcessBuiltIns: List<Output<ApiRequest, ApiResult>> = listOf()

    /**
     * Provides access to naming conventions used for actions
     */
    fun rename(text: String): String = settings.naming?.rename(text) ?: text

    fun setApiContainerHost() {
        routes.visitApis { _, api -> ApiServer.setApiHost(api.singleton, this) }
    }

    override fun host(): ApiServer = this

    /**
     * validates the request by checking for the api/action, and ensuring inputs are valid.
     *
     * @param req
     * @return
     */
    suspend fun check(request: ApiRequest): Outcome<Target> {
        return Calls.validateCall(request, { req -> get(req) })
    }

    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun get(cmd: Request): Notice<Target> {
        return getApi(cmd.area, cmd.name, cmd.action)
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
            execute(req, options)
        } catch (ex: Exception) {
            handleError(req, ex)
            val err = when(ex) {
                is ExceptionErr -> ex.err
                else -> Err.of(ex)
            }
            Outcomes.errored<Any>(err)
        }
        return result.toTry()
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun getApi(area: String, name: String, action: String): Notice<Target> {
        return routes.api(area, name, action, ctx)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun getApi(clsType: KClass<*>, member: KCallable<*>): Notice<Target> {
        val apiAnno = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)
        val result = apiAnno?.let { anno ->

            val area = anno.area
            val api = anno.name
            val actionAnno = Reflector.getAnnotationForMember<Action>(member, Action::class)
            val action = actionAnno?.let { act ->
                val action = if (act.name.isBlank()) member.name else act.name
                action
            } ?: member.name
            val info = getApi(area, api, action)
            info
        } ?: Notices.errored("member/annotation not found for : ${member.name}")
        return result
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
    private val hasChainedExecution = hooks.middleware.isNotEmpty()
    private val chainedExecutor = slatekit.functions.Processor.chain(hooks.middleware) { request ->
        executeMethod(Ctx.of(this, this.ctx, request), request)
    }
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
            .operate {
                processor.input(hooks.formatters  , it)
            }
            .operate {
                processor.input(preProcessBuiltIns, it)
            }

        // Step 4: Hooks: Pre-Processing Stage 2: run through more hooks ( API level & supplied )
        val requested = validated
            .operate { processor.input(preProcessAPIHooks, it) }
            .operate { processor.input(hooks.inputters, it)    }

        // Step 5: Execute request
        val executed = requested.flatMap { request ->
            if(hasChainedExecution) {
                chainedExecutor(request)
            } else {
                request.target?.let {
                    if(it.instance is slatekit.functions.Process<*,*>){
                        val processor = it.instance as slatekit.functions.Process<ApiRequest, ApiResult>
                        processor.process(request) {
                            executeMethod(Ctx.of(this, this.ctx, request), request)
                        }
                    }
                    else {
                        executeMethod(Ctx.of(this, this.ctx, request), request)
                    }
                } ?:executeMethod(Ctx.of(this, this.ctx, request), request)
            }
        }

        // Step 6: Hooks: Post-Processing Stage 1: errors hooks on API ( only if we mapped to a class.method )
        validated.onSuccess { Errors.applyError(rawRequest, it, requested, executed) }

        // Step 7: Hooks: Post-Processing Stage 2: remaining hooks ( afters, built-ins, outputters )
        val result = executed
            .operate { processor.output(rawRequest, requested, it, postProcessAPIHooks) }
            .operate { processor.output(rawRequest, requested, it, postProcessBuiltIns) }
            .operate { processor.output(rawRequest, requested, it, hooks.outputter)     }

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
        val apiReq = ApiRequest(this, ctx, req, null, this, null)
        logger.error("Unexpected error executing ${req.fullName}", ex)
    }

    companion object {

        @JvmStatic
        fun of(ctx: Context, apis: List<slatekit.apis.core.Api>, auth: Auth? = null, source: Source? = null): ApiServer {
            val hooks = ApiHooks(inputters = listOf(Authorize(auth)))
            val server = ApiServer(ctx, apis, hooks, ApiSettings(source ?: Source.Web))
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
        fun defaultPreHooks(): List<Input<ApiRequest>> {
            return listOf(
                Routing(),
                Targets(),
                Protos(),
                Validate()
            )
        }
    }

    val HELP = Status.Errored(10000, "help")
}
