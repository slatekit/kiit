package slatekit.apis


import slatekit.apis.core.*
import slatekit.apis.doc.DocConsole
import slatekit.apis.svcs.Format
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.helpers.ApiValidator
import slatekit.apis.helpers.ApiLoader
import slatekit.apis.middleware.*
import slatekit.common.args.ArgsFuncs
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.apis.support.Error
import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logger
import slatekit.common.results.HELP
import slatekit.common.results.ResultFuncs.notFound
import slatekit.meta.*
import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * This is the core container hosting, managing and executing the protocol independent apis.
 * @param ctx      :
 * @param allowIO  :
 * @param auth     :
 * @param protocol :
 * @param apis     :
 * @param errors   :
 * @param hooks    :
 * @param filters  :
 * @param controls :
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
     * The lookup/map for all the areas in the container
     * e.g. Slate Kit apis are in a 3 part route format :
     *
     *    e.g. area/api/action
     *         app/users/activate
     *
     * 1. area  : top level category containing 1 or more apis
     * 2. api   : an api represents a specific resource and has 1 or more actions
     * 3. action: the lowest level endpoint that maps to a method/function.
     *
     * NOTES:
     *
     * 1. The _lookup stores all the top level "areas" in the container
     *    as a mapping between area -> ApiLookup.
     * 2. The ApiLookup contains all the Apis as a mapping between "api" names to
     *    an ApiBase ( which is what you extend from to create your own api )
     * 3. The ApiBase then has a lookup of all "actions" mapped to methods.
     */
    val routes = Routes(ApiLoader.loadAll(apis, namer), namer, { api ->
        ApiContainer.setApiHost(api, this)
    })


    /**
     * The validator for requests, checking protocol, parameter validation, etc
     */
    private val _validator = Validation(this)


    /**
     * The list of rewriters
     */
    private val rewrites: List<Rewriter>? = middleware?.filter { it is Rewriter }?.map { it as Rewriter }


    val filters = middleware?.filter { it is Filter }?.map { it as Filter } ?: listOf()


    /**
     * The error handler that responsible for several expected errors/bad-requests
     * and also to handle unexpected errors
     */
    private val errs: slatekit.apis.middleware.Error? =
        middleware?.filter { it is Error }?.map { it as Error }?.firstOrNull()


    /**
     * The settings for the api ( limited for now )
     */
    val settings = ApiSettings()


    /**
     * The help class to handle help on an area, api, or action
     */
    val help = Help(this, routes, docBuilder)


    /**
     * Success flag to indicate to proceeed to call without a filter
     * This is pre-built to avoid rebuilding a static success flag each time
     */
    private val proceedOk: ResultEx<Any> = Success<Any>("")


    private val formatter = Format()


    private val emptyArgs = mapOf<String, Any>()


    private val logger: Logger = ctx.logs.getLogger("apis")


    fun rename(text: String): String = namer?.rename(text) ?: text


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
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun contains(area: String): Boolean {
        val parts = area.split('.')
        return when (parts.size) {
            0 -> false
            1 -> routes.contains(parts[0]) || routes.contains("", parts[0])
            2 -> routes.contains(parts[0], parts[1]) || routes.contains("", parts[0], parts[1])
            3 -> routes.contains(parts[0], parts[1], parts[2])
            else -> false
        }
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
            errs?.onError(ctx, req, req.path, this, ex, null)?.toResultEx() ?: Failure(ex)
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
        val req = Request.raw(area, api, action, verb, opts, args)
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
        if (area.isEmpty()) return badRequest("area not supplied")
        if (name.isEmpty()) return badRequest("api not supplied")
        if (action.isEmpty()) return badRequest("action not supplied")
        if (!routes.contains(area, name, action)) return badRequest("api route $area $name $action not found")

        val api = routes.api(area, name)!!
        val act = api.actions[action]!!
        val instance = routes.instance(area, name, ctx)
        return instance?.let { inst ->
            success(ApiRef(api, act, inst))
        } ?: badRequest("api route $area $name $action not found")
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
        val helpCheck = isHelp(raw)
        if (helpCheck.code == HELP) {
            return buildHelp(raw, helpCheck).toResultEx()
        }

        // Check 2: Check for a rewrites ( e.g. restify get /movies => get /movies/getAll )
        val rewrittenReq = convertRequest(raw)

        // Check 3: Finally check for formats ( e.g. recentMovies.csv => recentMovies -format=csv
        val req = formatter.rewrite(ctx, rewrittenReq, this, emptyArgs)
        val result: Result<Any, Exception> = try {
            val res1 = _validator.validateApi(req)
            val res = res1.flatMap { apiRef ->
                val runCtx = Ctx(this, this.ctx, req, apiRef)
                Exec(runCtx, _validator, logger).run(::executeMethod)
            }
            res.toResultEx()
        } catch (ex: Exception) {
            val api = routes.api(req.area, req.name)
            val apiRef = getApi(req.area, req.name, req.action)
            logger.error("Unexpected error with api request ${req.fullName}", ex)
            handleError(api, apiRef.getOrElse { null }, req, ex)
        }

        // Log failures
        result.onFailure {
            logger.error("Error on api request ${req.fullName} : ${result.msg}", it)
        }

        // Finally: If the format of the content specified ( json | csv | props )
        // Then serialize it here and return the content
        return convertResult(req, result)
    }


    @Suppress("UNCHECKED_CAST")
    protected open fun executeMethod(runCtx:Ctx): ResultEx<Any> {
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


    protected open fun handleError(
        api: slatekit.apis.core.Api?,
        apiRef: ApiRef?,
        req: Request,
        ex: Exception
    ): ResultEx<Any> {
        // OPTION 1: Api level
        return if (apiRef != null && apiRef.instance is slatekit.apis.middleware.Error) {
            logger.debug("Handling error at api level")
            apiRef.instance.onError(this.ctx, req, apiRef, this, ex, null).toResultEx()
        }
        // OPTION 2: GLOBAL Level custom handler
        else if (errs != null) {
            logger.debug("Handling error at global middleware")
            errs.onError(ctx, req, req.path, this, ex, null).toResultEx()
        }
        // OPTION 3: GLOBAL Level default handler
        else {
            logger.debug("Handling error at global container")
            handleErrorInternally(req, ex)
        }
    }


    /**
     * global handler for an unexpected error ( for derived classes to override )
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param ex     : the exception
     * @return
     */
    fun handleErrorInternally(req: Request, ex: Exception): ResultEx<Any> {
        val msg = "error executing : " + req.path + ", check inputs"
        return unexpectedError(Exception(msg, ex))
    }


    open fun isHelp(req: Request): ResultMsg<String> {

        // Case 3a: Help ?
        return if (ArgsFuncs.isHelp(req.parts, 0)) {
            ResultFuncs.help(msg = "?")
        }
        // Case 3b: Help on area ?
        else if (ArgsFuncs.isHelp(req.parts, 1)) {
            ResultFuncs.help(msg = "area ?")
        }
        // Case 3c: Help on api ?
        else if (ArgsFuncs.isHelp(req.parts, 2)) {
            ResultFuncs.help(msg = "area.api ?")
        }
        // Case 3d: Help on action ?
        else if (ArgsFuncs.isHelp(req.parts, 3)) {
            ResultFuncs.help(msg = "area.api.action ?")
        } else {
            failure("Unknown help option")
        }
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
        return if (!isDocKeyAvailable(req)) {
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


    protected open fun convertRequest(req: Request): Request {
        val finalRequest = rewrites?.fold(req, { acc, rewriter -> rewriter.rewrite(ctx, acc, this, emptyArgs) })
        return finalRequest ?: req
    }


    /**
     * Finally: If the format of the content specified ( json | csv | props )
     * Then serialize it here and return the content
     */
    protected open fun convertResult(req: Request, result: ResultEx<Any>): ResultEx<Any> {
        return if (result.success && !req.output.isNullOrEmpty()) {
            val finalSerializer = serializer ?: this::serialize
            val serialized = finalSerializer(req.output ?: "", result.getOrElse { null })
            (result as Success).copy(data = serialized!!)
        } else {
            result
        }
    }


    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    fun serialize(format: String, data: Any?): Any? {

        val content = when (format) {
            ContentTypeCsv.ext -> Content.csv(Serialization.csv().serialize(data))
            ContentTypeJson.ext -> Content.json(Serialization.json().serialize(data))
            ContentTypeProp.ext -> Content.prop(Serialization.props(true).serialize(data))
            else -> data
        }
        return content
    }


    fun isDocKeyAvailable(req: Request): Boolean {
        // Ensure that docs are only available w/ help key
        val docKeyValue = if (req.meta.containsKey(ApiConstants.DocKeyName)) {
            req.meta.get(ApiConstants.DocKeyName) ?: ""
        } else if (req.data.containsKey(ApiConstants.DocKeyName)) {
            req.data.get(ApiConstants.DocKeyName) ?: ""
        } else
            ""
        return docKeyValue == docKey
    }


    fun isCliAllowed(supportedProtocol: String): Boolean =
        supportedProtocol == ApiConstants.Any || supportedProtocol == ApiConstants.SourceCLI


    companion object {

        fun setApiHost(item: Any?, host: ApiContainer) {
            if (item is ApiHostAware) {
                item.setApiHost(host)
            }
        }
    }
}
