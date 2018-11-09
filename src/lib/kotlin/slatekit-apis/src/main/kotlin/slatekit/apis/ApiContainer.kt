package slatekit.apis

import slatekit.apis.core.*
import slatekit.apis.doc.DocConsole
import slatekit.apis.helpers.*
import slatekit.apis.svcs.Format
import slatekit.apis.middleware.*
import slatekit.apis.security.Protocol
import slatekit.apis.security.WebProtocol
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.apis.support.Error
import slatekit.common.*
import slatekit.common.content.Content
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logger
import slatekit.common.naming.Namer
import slatekit.common.results.ResultCode.HELP
import slatekit.common.results.ResultFuncs.notFound
import slatekit.meta.*
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
open class ApiContainer(
    val ctx: Context,
    val allowIO: Boolean,
    val auth: Auth? = null,
    val protocol: Protocol = WebProtocol,
    val apis: List<slatekit.apis.core.Api> = listOf(),
    val namer: Namer? = null,
    val middleware: List<Middleware>? = null,
    val deserializer: ((Request, Encryptor?) -> Deserializer)? = null,
    val serializer: ((String, Any?) -> String)? = null,
    val docKey: String? = null,
    val docBuilder: () -> slatekit.apis.doc.Doc = ::DocConsole
) {

    /**
     * Load all the routes from the APIs supplied.
     * The API setup can be either annotation based or public methods on the Class
     */
    val routes = Routes(ApiLoader.loadAll(apis, namer), namer)

    /**
     * Load all global middleware components: Rewriters, Filters, Trackers, Error handlers
     */
    val rewrites = middleware?.filter { it is Rewriter }?.map { it as Rewriter } ?: listOf()
    val filters = middleware?.filter { it is Filter }?.map { it as Filter } ?: listOf()
    val tracker = middleware?.filter { it is Tracked }?.map { it as Tracked }?.firstOrNull()
    val errs = middleware?.filter { it is Error }?.map { it as Error }?.firstOrNull()

    val results by lazy { ApiResults(ctx, this, rewrites, serializer) }

    /**
     * The settings for the api ( limited for now )
     */
    val settings: ApiSettings by lazy { ApiSettings() }

    /**
     * The help class to handle help on an area, api, or action
     */
    val help: Help by lazy { Help(this, routes, docBuilder) }

    /**
     * The validator for requests, checking protocol, parameter validation, etc
     */
    private val _validator by lazy { Validation(this) }

    private val formatter = Format()

    private val emptyArgs = mapOf<String, Any>()

    private val logger: Logger = ctx.logs.getLogger("api")

    val errorHandler: Errors = Errors(logger)

    fun rename(text: String): String = namer?.rename(text) ?: text

    fun setApiContainerHost() {
        routes.visitApis{ _, api -> ApiContainer.setApiHost(api.singleton, this) }
    }

    /**
     * validates the request by checking for the api/action, and ensuring inputs are valid.
     *
     * @param req
     * @return
     */
    fun check(request: Request): ResultMsg<ApiRef> {
        return ApiValidator.validateCall(request, { req -> get(req) })
    }

    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun get(cmd: Request): ResultMsg<ApiRef> {
        return getApi(cmd.area, cmd.name, cmd.action)
    }

    fun sample(cmd: Request, path: File): ResultMsg<String> {
        val action = get(cmd)
        val sample = if (action.success) {
            val parameters = when (action) {
                is Success -> action.data.action.paramsUser
                is Failure -> listOf()
            }
            val serializer = Serialization.sampler() as SerializerSample
            val text = serializer.serializeParams(parameters)
            text
        } else "Unable to find command: " + cmd.path

        path.writeText(sample)
        return success("sample call written to : ${path.absolutePath}")
    }

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    fun call(req: Request): Response<Any> {
        return callAsResult(req).toResponse()
    }

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    fun callAsResult(req: Request): ResultEx<Any> {
        val result: ResultEx<Any> = try {
            execute(req)
        } catch (ex: Exception) {
            logger.error("Unexpected error executing ${req.fullName}", ex)
            errs?.onError(ctx, req, req.path, this, ex, null)?.toResultEx()
            Failure(ex)
        }
        return result
    }

    fun call(
        area: String,
        api: String,
        action: String,
        verb: String,
        opts: Map<String, Any>,
        args: Map<String, Any>
    ): ResultEx<Any> {
        val req = Request.cli(area, api, action, verb, opts, args)
        return callAsResult(req)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun getApi(area: String, name: String, action: String): ResultMsg<ApiRef> {
        return routes.api(area, name, action, ctx)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun getApi(clsType: KClass<*>, member: KCallable<*>): ResultMsg<ApiRef> {
        val apiAnno = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)
        val result = apiAnno?.let { anno ->

            val area = anno.area
            val api = anno.name
            val actionAnno = Reflector.getAnnotationForMember<ApiAction>(member, ApiAction::class)
            val action = actionAnno?.let { act ->
                val action = if (act.name.isBlank()) member.name else act.name
                action
            } ?: member.name
            val info = getApi(area, api, action)
            info
        } ?: notFound("member/annotation not found for : ${member.name}")
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
    protected fun execute(raw: Request): ResultEx<Any> {
        // Check 1: Check for help / discovery
        val helpCheck = ApiUtils.isHelp(raw)
        if (helpCheck.code == HELP) return buildHelp(raw, helpCheck).toResultEx()

        // Rewrites ( e.g. restify get /movies => get /movies/getAll )
        val rewrittenReq = results.convert(raw)

        // Formats ( e.g. recentMovies.csv => recentMovies -format=csv
        val req = formatter.rewrite(ctx, rewrittenReq, this, emptyArgs)

        // Api exists ?
        val apiCheck = _validator.validateApi(req)

        // Execute the API using
        val resultRaw = apiCheck.flatMap { apiRef ->

            // Run context to store all relevant info
            val runCtx = Ctx(this, this.ctx, req, apiRef)

            // Execute using a pipeline
            Exec(runCtx, _validator, logger).run(::executeMethod)
        }
        val result = resultRaw.toResultEx()

        // Finally: If the format of the content specified ( json | csv | props )
        // Then serialize it here and return the content
        return results.convert(req, result)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun executeMethod(runCtx: Ctx): ResultEx<Any> {
        // Finally make call.
        val req = runCtx.req
        val apiRef = runCtx.apiRef
        val converter = deserializer?.invoke(req, ctx.enc) ?: Deserializer(req, ctx.enc)
        val inputs = ApiHelper.fillArgs(converter, apiRef, req)

        val returnVal = Reflector.callMethod(apiRef.api.cls, apiRef.instance, apiRef.action.member.name, inputs)

        return returnVal?.let { res ->
            if (res is Result<*, *>) {
                (res as Result<Any, Any>).toResultEx()
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
    open fun buildHelp(req: Request, result: ResultMsg<String>): ResultMsg<Content> {
        return if (!ApiUtils.isDocKeyed(req, docKey ?: "")) {
            failure("Unauthorized access to API docs")
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
            success(Content.html(content))
        }
    }


    companion object {

        @JvmStatic
        fun setApiHost(item: Any?, host: ApiContainer) {
            if (item is ApiHostAware) {
                item.setApiHost(host)
            }
        }
    }
}
