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

import slatekit.common.Failure
import slatekit.common.Result
import slatekit.common.Success


object ResultFuncs {

    /**
     * Help result : return success with string value "help" and status code of HELP
     * @param msg : Optional message
     * @return
     */
    fun help(msg: String = "success"): Result<String> {
        return Failure(HELP, err = null, msg = msg)
    }


    /**
     * Help result : return success with string value "help" and status code of HELP
     * @param msg : Optional message
     * @return
     */
    fun <T> helpOn(msg: String = "success"): Result<T> {
        return Failure(HELP, err = null, msg = msg)
    }


    /**
     * Exit result : return failure with string value "exit" and status code of EXIT
     * @param msg : Optional message
     * @return
     */
    fun exit(msg: String = "exit"): Result<String> {
        return Failure(EXIT, err = null, msg = msg)
    }


    /**
     * Boolean result : return a SomeResult with bool value of true
     * @param msg : Optional message
     * @return
     */
    fun ok(msg: String = ""): Result<Boolean> {
        return Success(SUCCESS, true, msg = msg)
    }


    /**
     * Boolean result : return a SomeResult with bool value of true
     * @param msg : Optional message
     * @return
     */
    fun yes(msg: String = ""): Result<Boolean> {
        return Success(SUCCESS, true, msg = msg)
    }


    /**
     * Boolean result : return a SomeResult with bool value of true
     * @param code : Code
     * @param msg  : Optional message
     * @return
     */
    fun yesWithCode(code: Int,
                    msg: String = ""): Result<Boolean> {
        return Success(code, true, msg = msg)
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
    fun no(msg: String = ""): Result<Boolean> {
        return Failure(FAILURE, err = null, msg = msg)
    }


    /**
     * Boolean result : return a SomeResult with bool value of false
     * @param msg : Optional message
     * @return
     * @note      : This is not to be confused as an error result, but a legitimate
     *              return value of false ( along with the message, tag, etc being
     *              available as part of the Result<T> class.
     */
    fun err(msg: String): Result<Boolean> {
        return Failure(FAILURE, err = null, msg = msg)
    }


    fun okOrFailure(callback: () -> String): Result<Boolean> {
        return try {
            val msg = callback()
            yes(msg)
        }
        catch(ex: Exception) {
            failure(msg = ex.message ?: "", err = ex)
        }
    }


    /**
     * Builds either a SuccessResult with true value or an FailureResult based on the success flag.
     * @param msg : Optional message
     * @return
     */
    fun okOrFailure(success: Boolean,
                    msg: String = ""): Result<Boolean> {
        return if (success)
            Success(SUCCESS, true, msg)
        else
            Failure(FAILURE, msg = msg, err = null)
    }

    //
    //  /**
    //   * Builds either a SuccessResult or an FailureResult based on the success flag.
    //   * @param msg : Optional message
    //   * @return
    //   */
    //  fun <T> successOrErrorWithCode<T>(success:Boolean,
    //                                value:T,
    //                                code:Int,
    //                                msg:String? = null,
    //                                ref:Any?    = null): Result<T>
    //  {
    //    return if(success)
    //      Result<T>(value, code, msg, tag)
    //    else
    //      Result<T>(code, msg = msg, err = null)
    //  }
    //
    //
    //  fun <T> successOr( value : T,
    //                     code : Int,
    //                     msg  : String? = "not found"
    //                   ): Result<T> = {
    //    value.fold[Result<T>](Result<T>(code, msg))( d => {
    //      Result<T>(d, code = SUCCESS)
    //    })
    //  }
    //
    //
    /**
    //   * Builds either a SuccessResult or an FailureResult based on the success flag.
    //   * @param msg : Optional message
    //   * @return
    //   */
    fun <T> successOrError(success: Boolean,
                           value: T?,
                           msg: String = ""): Result<T> {
        return if (success && value != null)
            Success(SUCCESS, value, msg = msg)
        else
            Failure(FAILURE, err = null, msg = msg)
    }


    fun <T> successOrError(callback: () -> T): Result<T> {
        return try {
            val v = callback()
            Success(SUCCESS, v)
        }
        catch(ex: Exception) {
            Failure(FAILURE, ex, ex.message ?: "")
        }
    }


    /**
     * Builds an FailureResult with no value, and error code of NOT_IMPLEMENTED
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> success(data: T,
                    msg: String = "success"): Result<T> {
        return Success(SUCCESS, data, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and error code of NOT_IMPLEMENTED
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> successWithCode(code: Int,
                            data: T,
                            msg: String = "success"): Result<T> {
        return Success(code, data, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to CONFIRM
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> confirm(data: T,
                    msg: String = "confirm"): Result<T> {
        return Success(CONFIRM, data, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to FAILURE
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> failure(msg: String = "failure",
                    err: Exception? = null): Result<T> {
        return Failure(FAILURE, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to FAILURE
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> failureWithCode(code: Int,
                            msg: String = "failure",
                            err: Exception? = null): Result<T> {
        return Failure(code, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to UNAUTHORIZED
     * @param msg : Optional message
     * @return
     */
    fun <T> unAuthorized(msg: String = "unauthorized",
                         err: Exception? = null): Result<T> {
        return Failure(UNAUTHORIZED, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_FOUND
     * @param msg : Optional message
     * @return
     */
    fun <T> notFound(msg: String = "not found",
                     err: Exception? = null): Result<T> {
        return Failure(NOT_FOUND, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to BAD_REQUEST
     * @param msg : Optional message
     * @param tag : Optional tag
     * @return
     */
    fun <T> badRequest(msg: String = "bad request",
                       err: Exception? = null): Result<T> {
        return Failure(BAD_REQUEST, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to CONFLICT
     * @param msg : Optional message
     * @return
     */
    fun <T> conflict(msg: String = "conflict",
                     err: Exception? = null): Result<T> {
        return Failure(CONFLICT, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to DEPRECATED
     * @param msg : Optional message
     * @return
     */
    fun <T> deprecated(msg: String = "deprecated",
                       err: Exception? = null): Result<T> {
        return Failure(DEPRECATED, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to UNEXPECTED_ERROR
     * @param msg : Optional message
     * @return
     */
    fun <T> unexpectedError(msg: String = "unexpected error",
                            err: Exception? = null): Result<T> {
        return Failure(UNEXPECTED_ERROR, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_AVAILABLE
     * @param msg : Optional message
     * @return
     */
    fun <T> notAvailable(msg: String = "not available",
                         err: Exception? = null): Result<T> {
        return Failure(NOT_AVAILABLE, err, msg = msg)
    }


    /**
     * Builds an FailureResult with no value, and with code set to NOT_IMPLEMENTED
     * @param msg : Optional message
     * @return
     */
    fun <T> notImplemented(msg: String = "not implemented",
                           err: Exception? = null): Result<T> {
        return Failure(NOT_IMPLEMENTED, err, msg = msg)
    }
}
