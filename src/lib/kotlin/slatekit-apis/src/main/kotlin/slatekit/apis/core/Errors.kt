/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.core

import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.unexpectedError


class Errors(val callback: ((Context, Request, Any, Exception) -> Result<Any>)?) {

    /**
     * handler for when an "area" is not found
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param result : the result of the last validation check
     */
    fun areaNotFound(ctx: Context, req: Request, source:Any, result: Result<Any>): Unit {
        invalidRequest("api", "api area not found", req.path, result)
    }


    /**
     * handler for when an "api" is not found
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param result : the result of the last validation check
     */
    fun apiNotFound(ctx: Context, req: Request, source:Any, result: Result<Any>): Unit {
        invalidRequest("api", "api action not found, check api/action name(s)", req.path, result)
    }


    /**
     * handler for when an action is not found
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param result : the result of the last validation check
     */
    fun actionNotFound(ctx: Context, req: Request, source:Any, result: Result<Any>): Unit {
        invalidRequest("api", "action not found", req.path, result)
    }


    /**
     * callback for when the action to call failed
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param result : the result of the last validation check
     */
    fun actionFailed(ctx: Context, req: Request, source:Any, result: Result<Any>): Unit {
        invalidRequest("api", "api action call failed, check api action input(s)", req.action, result)
    }


    /**
     * handler for when the input
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param result : the result of the last validation check
     */
    fun actionInputsInvalid(ctx: Context, req: Request, source:Any, result: Result<Any>): Unit {
        invalidRequest("inputs", "Invalid inputs supplied", req.path, result)
    }


    /**
     * handler for an unexpected error ( for derived classes to override )
     *
     * @param ctx    : the application context
     * @param req    : the request
     * @param ex     : the exception
     * @return
     */
    fun error(ctx: Context, req: Request, source:Any, ex: Exception): Result<Any> {

        fun buildUnexpected(): Result<Any> {
            return unexpectedError(msg = "error executing : " + req.path + ", check inputs")
        }
        return callback?.let { call ->
            call(ctx, req, source, ex)
        } ?: buildUnexpected()
    }


    /**
     * handler for an expected / known error
     * @param errorType
     * @param message
     * @param path
     * @param result
     */
    fun invalidRequest(errorType: String, message: String, path: String, result: Result<Any>): Unit {
        // For derived classes to override
    }
}
