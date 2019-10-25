package slatekit.apis

import slatekit.apis.core.*
import slatekit.apis.core.Target
import slatekit.apis.tools.docs.DocConsole
import slatekit.apis.helpers.*
import slatekit.apis.support.ExecSupport
import slatekit.apis.hooks.Targets
import slatekit.apis.setup.HostAware
import slatekit.common.*
import slatekit.common.content.Content
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logger
import slatekit.common.naming.Namer
import slatekit.common.requests.Request
import slatekit.functions.middleware.Middleware
import slatekit.meta.*
import slatekit.results.*
import slatekit.results.Status
import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries
import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * This is the core container hosting, managing and executing the protocol independent apis.
 * @param ctx: 
 * @param allowIO: 
 * @param auth: 
 * @param protocol: 
 * @param apis: 
 * @param errors: 
 * @param hooks: 
 * @param filters: 
 * @param controls: 
 */
open class ApiHost(
        val ctx: Context,
        val allowIO: Boolean,
        val auth: Auth? = null,
        val protocol: Protocol = Protocol.Web,
        val apis: List<slatekit.apis.core.Api> = listOf(),
        val namer: Namer? = null,
        val middleware: List<Middleware>? = null,
        val deserializer: ((Request, Encryptor?) -> Deserializer)? = null,
        val serializer: ((String, Any?) -> String)? = null,
        val docKey: String? = null,
        val docBuilder: () -> slatekit.apis.tools.docs.Doc = ::DocConsole
) : ExecSupport {

    /**
     * Load all the routes from the APIs supplied.
     * The API setup can be either annotation based or public methods on the Class
     */
    val routes = Routes(ApiLoader.loadAll(apis, namer), namer)

    val results by lazy { ApiResults(ctx, this, serializer) }

    /**
     * The settings for the api ( limited for now )
     */
    val settings: ApiSettings by lazy { ApiSettings() }

    /**
     * The help class to handle help on an area, api, or action
     */
    val help: Help by lazy { Help(this, routes, docBuilder) }

    private val emptyArgs = mapOf<String, Any>()

    private val logger: Logger = ctx.logs.getLogger("api")

    fun rename(text: String): String = namer?.rename(text) ?: text

    fun setApiContainerHost() {
        routes.visitApis{ _, api -> ApiHost.setApiHost(api.singleton, this) }
    }


    override fun host(): ApiHost = this


    /**
     * validates the request by checking for the api/action, and ensuring inputs are valid.
     *
     * @param req
     * @return
     */
    suspend fun check(request: ApiRequest): Outcome<Target> {
        return ApiValidator.validateCall(request, { req -> get(req) })
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
    suspend fun call(req: Request, options:ExecOptions?): Try<Any> {
        val result = Tries.attempt {
            execute(req, options)
        }
        result.onFailure {
            handleError(req, it)
        }
        return result
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
     * 1. middleware        : rewrite request
     * 2. validate api      : validate api actually exists
     * 3. validate protocol : validate the request came from a valid protocol ( cli | web | etc )
     * 4. validate auth     : validate the authorization using auth trait
     * 5. validate params   : validate the request against the api/method parameters ( just that they exist )
     * 6. execute           : finally execute the api action with middleware ( with filter / hooks )
     *                        currently the filter/hooks middleware must be implemented in the api itself.
     * @param cmd
     * @return
     */
    suspend fun execute(raw: Request, options:ExecOptions? = null): Try<Any> {
        // Check 1: Check for help / discovery
        val helpCheck = raw.isHelp()
        if (helpCheck.code == HELP.code) return buildHelp(raw, helpCheck).toTry()

        // Rewrites ( e.g. restify get /movies => get /movies/getAll )
        //val rewrittenReq = results.convert(raw)

        // Formats ( e.g. recentMovies.csv => recentMovies -format=csv
        val apiReqRaw = ApiRequest(this, ctx, raw, null, raw.source, emptyArgs)
        val req = settings.inputters.first().process(Outcomes.success(apiReqRaw))

        // Api exists ?
        val apiCheck = Targets().process(req)

        // Execute the API using
        val resultRaw = apiCheck.flatMap { apiReq ->

            // Run context to store all relevant info
            val runCtx = Ctx(this, this.ctx, raw, apiReq.target!!)

            Exec(runCtx, apiReq, logger, options).run(this::executeMethod)
        }
        val result = resultRaw.toTry()

        // Finally: If the format of the content specified ( json | csv | props )
        // Then serialize it here and return the content
        return results.convert(apiReqRaw.request, result)
    }

    @Suppress("UNCHECKED_CAST")
    protected open suspend fun executeMethod(runCtx: Ctx): Try<Any> {
        // Finally make call.
        val req = runCtx.req
        val target = runCtx.target
        val converter = deserializer?.invoke(req, ctx.enc) ?: Deserializer(req, ctx.enc)
        val inputs = ApiHelper.fillArgs(converter, target, req)

        val returnVal = Reflector.callMethod(target.api.cls, target.instance, target.action.member.name, inputs)

        return returnVal?.let { res ->
            if (res is Result<*, *>) {
                (res as Result<Any, Any>).toTry()
            } else {
                Success(res)
            }
        } ?: Failure(Exception("Received null"))
    }

    /**
     * Handles help request on any part of the api request. Api requests are typically in
     * the format "area.api.action" so you can type help on each part / region.
     * e.g.
     * 1. area ?
     * 2. area.api ?
     * 3. area.api.action ?
     * @param cmd
     * @param mode
     */
    open fun buildHelp(req: Request, result: Outcome<String>): Outcome<Content> {
        return if (!req.hasDocKey(docKey ?: "")) {
            Outcomes.errored("Unauthorized access to API docs")
        } else {
            val content = when (result.msg) {
                // 1: {area} ? = help on area
                "?" -> {
                    help.help()
                }
                // 2: {area} ? = help on area
                "area ?" -> {
                    help.area(req.parts[0])
                }
                // 3. {area}.{api} = help on api
                "area.api ?" -> {
                    help.api(req.parts[0], req.parts[1])
                }
                // 3. {area}.{api}.{action} = help on api action
                else -> {
                    help.action(req.parts[0], req.parts[1], req.parts[2])
                }
            }
            Outcomes.success(Content.html(content))
        }
    }


    private suspend fun handleError(req:Request, ex:Exception)  {
        val apiReq = ApiRequest(this, ctx, req, null, this, null)
        logger.error("Unexpected error executing ${req.fullName}", ex)
    }


    companion object {

        @JvmStatic
        fun setApiHost(item: Any?, host: ApiHost) {
            if (item is HostAware) {
                item.setApiHost(host)
            }
        }
    }


    val HELP = Status.Errored(10000, "help")
}
