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

package slatekit.common.results

import slatekit.common.*
import slatekit.common.results.ResultCode.BAD_REQUEST
import slatekit.common.results.ResultCode.CONFIRM
import slatekit.common.results.ResultCode.CONFLICT
import slatekit.common.results.ResultCode.DEPRECATED
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.HELP
import slatekit.common.results.ResultCode.MISSING
import slatekit.common.results.ResultCode.NOT_AVAILABLE
import slatekit.common.results.ResultCode.NOT_FOUND
import slatekit.common.results.ResultCode.NOT_IMPLEMENTED
import slatekit.common.results.ResultCode.SUCCESS


object ResultFuncs {

    /**
     * Help result : return success with string value "help" and status code of HELP
     * @param msg : Optional message
     * @return
     */
    fun help(msg: String = "success"): ResultMsg<String> {
        return Failure(msg, HELP, msg)
    }


    /**
     * Help result : return success with string value "help" and status code of HELP
     * @param msg : Optional message
     * @return
     */
    fun <T> helpOn(msg: String = "success"): ResultMsg<T> {
        return Failure(msg, HELP, msg)
    }


    /**
     * Exit result : return failure with string value "exit" and status code of EXIT
     * @param msg : Optional message
     * @return
     */
    fun exit(msg: String = "exit"): ResultMsg<String> {
        return Failure(msg, EXIT, msg)
    }


    /**
     * Boolean result : return a SomeResult with bool value of true
     * @param msg  : Optional message
     * @param code : Optional code
     * @return
     */
    fun yes(msg: String = "", code:Int = SUCCESS): ResultMsg<Boolean> {
        return Success(true, code, msg)
    }


    /**
     * Boolean result : return a SomeResult with bool value of false
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     * @note      : This is not to be confused as an error result, but a legitimate
     *              return value of false ( along with the message, tag, etc being
     *              available as part of the Result<T> class.
     */
    fun no(msg: String = "", code:Int = FAILURE): ResultMsg<Boolean> {
        return Failure(msg, code, msg)
    }


    /**
     *   Builds either a SuccessResult or an FailureResult based on the success flag.
     *   @param msg : Optional message
     *   @return
     *
     */
    fun <T> successOrError(success: Boolean,
                           value: T?,
                           msg: String = ""): ResultMsg<T> {
        return if (success && value != null)
            Success(value, SUCCESS, msg)
        else
            Failure(msg, FAILURE, msg)
    }


    fun <T> successOrError(callback: () -> T): ResultEx<T> {
        return try {
            val v = callback()
            Success(v, SUCCESS)
        }
        catch(ex: Exception) {
            Failure(ex, FAILURE, ex.message ?: "")
        }
    }


    /**
     * Builds an FailureResult with no value, and error code of NOT_IMPLEMENTED
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> success(data: T,
                    code:Int = SUCCESS,
                    msg: String = "success"): ResultMsg<T> {
        return Success(data, code, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to CONFIRM
     * @param data : Optional data
     * @param msg : Optional msg
     * @return
     */
    fun <T> confirm(data: T,
                    msg: String = "confirm"): ResultMsg<T> {
        return Success(data, CONFIRM,  msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to FAILURE
     * @param msg  : Optional message
     * @param code : Optional code indicating
     * @return
     */
    fun <T> failure(msg: String = "failure",
                    code:Int = FAILURE): ResultMsg<T> {
        return Failure(msg, code, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to BAD_REQUEST
     * @param msg : Optional message
     * @return
     */
    fun <T> badRequest(msg: String = "bad request"): ResultMsg<T> {
        return Failure(msg, BAD_REQUEST, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to UNAUTHORIZED
     * @param msg : Optional message
     * @return
     */
    fun <T> unAuthorized(msg: String = "unauthorized"): ResultMsg<T> {
        return Failure(msg, UNAUTHORIZED, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_FOUND
     * @param msg : Optional message
     * @return
     */
    fun <T> notFound(msg: String = "not found"): ResultMsg<T> {
        return Failure(msg, NOT_FOUND, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_FOUND
     * @param msg : Optional message
     * @return
     */
    fun <T> missing(msg: String = "not found"): ResultMsg<T> {
        return Failure(msg, MISSING, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to CONFLICT
     * @param msg : Optional message
     * @return
     */
    fun <T> conflict(msg: String = "conflict"): ResultMsg<T> {
        return Failure(msg, CONFLICT, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to DEPRECATED
     * @param msg : Optional message
     * @return
     */
    fun <T> deprecated(msg: String = "deprecated"): ResultMsg<T> {
        return Failure(msg, DEPRECATED, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_AVAILABLE
     * @param msg : Optional message
     * @return
     */
    fun <T> notAvailable(msg: String = "not available"): ResultMsg<T> {
        return Failure(msg, NOT_AVAILABLE, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_IMPLEMENTED
     * @param msg : Optional message
     * @return
     */
    fun <T> notImplemented(msg: String = "not implemented"): ResultMsg<T> {
        return Failure(msg, NOT_IMPLEMENTED, msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to UNEXPECTED_ERROR
     * @param msg : Optional message
     * @return
     */
    fun <T> unexpectedError(err: Exception,
                            msg: String = "unexpected error"): ResultEx<T> {
        return Failure(err, UNEXPECTED_ERROR, msg)
    }
}
