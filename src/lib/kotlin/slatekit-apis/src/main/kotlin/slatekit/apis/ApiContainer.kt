package slatekit.apis

import slatekit.apis.core.*
import slatekit.apis.doc.DocConsole
import slatekit.apis.support.ApiHelper
import slatekit.apis.support.ApiValidator
import slatekit.apis.support.Areas
import slatekit.common.*
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.okOrFailure
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.core.common.AppContext
import slatekit.core.middleware.Filter
import slatekit.core.middleware.Handler
import slatekit.core.middleware.Hook

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
        val ctx: AppContext,
        val allowIO: Boolean,
        val auth: Auth? = null,
        val protocol: ApiProtocol = ApiProtocolAny,
        val apis: List<ApiReg>? = null,
        val errors: Errors? = null,
        val hooks: List<Hook>? = null,
        val filters: List<Filter>? = null,
        val controls: List<Handler>? = null
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
     * 3. action: the lowest level endpoint that maps to a scala method/function.
     *
     * NOTES:
     *
     * 1. The _lookup stores all the top level "areas" in the container
     *    as a mapping between area -> ApiLookup.
     * 2. The ApiLookup contains all the Apis as a mapping between "api" names to
     *    an ApiBase ( which is what you extend from to create your own api )
     * 3. The ApiBase then has a lookup of all "actions" mapped to scala methods.
     */
    protected val _lookup = Areas().registerAll(apis)


    /**
     * The validator for requests, checking protocol, parameter validation, etc
     */
    protected val _validator = Validation(this)


    /**
     * The error handler that responsible for several expected errors/bad-requests
     * and also to handle unexpected errors
     */
    val errs = errors ?: Errors(null)


    /**
     * The settings for the api ( limited for now )
     */
    val settings = ApiSettings()


    /**
     * The help class to handle help on an area, api, or action
     */
    val help = Help(this, _lookup, DocConsole())


    /**
     * Success flag to indicate to proceeed to call without a filter
     * This is pre-built to avoid rebuilding a static success flag each time
     */
    val proceedOk = ok()


    fun register(reg: ApiReg): Unit {
        _lookup.register(reg)
    }


    /**
     * register via an explicit API
     */
    fun register(api: ApiBase): Unit {
        _lookup.register(api)
    }


    /**
     * validates the request by checking for the api/action, and ensuring inputs are valid.
     *
     * @param req
     * @return
     */
    fun check(req: Request): Result<Boolean> {
        val result = ApiValidator.validateCall(req, { req -> get(req) })
        return okOrFailure(result.success, msg = result.msg, tag = req.fullName)
    }


    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun get(cmd: Request): Result<Pair<Action, ApiBase>> {
        return getMappedAction(cmd.area, cmd.name, cmd.action)
    }


    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun get(area: String): ListMap<String, ApiBase>? = _lookup[area]


    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun contains(area: String): Boolean {
        val parts = area.split('.')
        return when (parts.size) {
            0    -> false
            1    -> _lookup.get(parts[0]) != null
            2    -> _lookup.get(parts[0])?.get(parts[1]) != null
            3    -> _lookup.get(parts[0])?.get(parts[1])?.get(parts[2]) != null
            else -> false
        }
    }


    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    fun call(req: Request): Result<Any> {
        val result: Result<Any> = try {
            execute(req)
        }
        catch(ex: Exception) {
            errs.error(ctx, req, ex)
        }
        return result
    }


    fun call(area: String, api: String, action: String, verb: String, opts: Map<String, Any>, args: Map<String, Any>): Result<Any> {
        val req = Request.raw(area, api, action, verb, opts, args)
        return call(req)
    }


    /**
     * gets the mapped scala method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun getMappedAction(area: String, name: String, action: String): Result<Pair<Action, ApiBase>> {
        if (area.isNullOrEmpty()) return badRequest("api area not supplied")
        if (name.isNullOrEmpty()) return badRequest("api name not supplied")
        if (action.isNullOrEmpty()) return badRequest("api action not supplied")

        val result = _lookup.get(area)?.get(name)?.let { apiBase ->
            val apiAction = apiBase.get(action)
            apiAction?.let { a ->
                success(Pair(a, apiBase))
            } ?: badRequest("api route $area $name $action not found")
        } ?: badRequest("api route $area $name $action not found")
        return result
    }


    /**
     * executes the api request in a pipe-line of various checks and validations.
     * @param cmd
     * @return
     */
    private fun execute(cmd: Request): Result<Any> {

        // TODO: Look at functional chaining to avoid the nesting via flatmap
        // E.g. something akin to scala's for comprehension.

        val result = try {
            val api = _validator.validateApi(cmd)
            api.flatMap { r ->
                val pro = _validator.validateProtocol(r.first, r.second, cmd)
                pro.flatMap { a ->
                    val auth = _validator.validateAuthorization(r.first, cmd)
                    auth.flatMap { au ->
                        val md = _validator.validateMiddleware(cmd)
                        md.flatMap { m ->
                            val pm = _validator.validateParameters(cmd)
                            executeWithMiddleware(cmd, r.second, r.first)
                        }
                    }
                }
            }
        }
        catch(ex: Exception) {
            val api = _lookup.get(cmd.area)?.get(cmd.name)
            handleError(api, cmd, ex)
            failure<String>("Unexpected error handling request: " + ex.message)
        }

        return result
    }


    /**
     * executes the api request factoring in the middleware filters and hooks.
     * @param req
     * @param api
     * @param action
     * @return
     */
    private fun executeWithMiddleware(req: Request, api: ApiBase, action: Action): Result<Any> {

        // Filter
        val proceed = if (api.isFilterEnabled) {
            api.onFilter(this.ctx, req, action)
        } else {
            proceedOk
        }

        // Ok to call.
        val callResult = if ( proceed.success ) {

            // Hook: Before
            if (api.isHookEnabled) {
                api.onBefore(this.ctx, req, action)
            }

            // Finally make the call here.
            val result = execute(req, api, action)

            // Hook: After
            if (api.isHookEnabled) {
                api.onAfter(this.ctx, req, action)
            }

            // Return the result
            result

        } else {
            proceed
        }
        return callResult
    }


    fun execute(req: Request, api: ApiBase, action: Action): Result<Any> {
        // Finally make call.
        val inputs = ApiHelper.fillArgs(action, req, req.args!!, allowIO, this.ctx.enc)
        val returnVal = Reflector.callMethod(api.kClass, api, req.action, inputs)

        val result = returnVal?.let { res ->
            if (res is Result<*>) {
                res as Result<Any>
            }
            else {
                success(res)
            }
        } ?: failure()

        // Return the result
        return result
    }


    fun handleError(api: ApiBase?, cmd: Request, ex: Exception): Result<Any> {
        // OPTION 1: Api level
        return if (api != null && api.isErrorEnabled) {
            api.onException(this.ctx, cmd, ex)
        }
        // OPTION 2: GLOBAL Level custom handler
        else if (errors != null) {
            errs.error(ctx, cmd, ex)
        }
        // OPTION 3: GLOBAL Level default handler
        else {
            error(ctx, cmd, ex)
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
    fun error(ctx: AppContext, req: Request, ex: Exception): Result<Any> {
        println(ex.message)
        return unexpectedError(msg = "error executing : " + req.path + ", check inputs")
    }


    fun isCliAllowed(cmd: Request, supportedProtocol: String): Boolean =
            supportedProtocol == "*" || supportedProtocol == "cli"

}