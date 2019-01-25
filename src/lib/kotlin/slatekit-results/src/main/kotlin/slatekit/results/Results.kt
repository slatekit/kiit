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

package slatekit.results


object Results {

    @JvmStatic fun <T> success(data: T): Result<T,Err> {
        return Success(data, Codes.SUCCESS.code, Codes.SUCCESS.msg)
    }


    @JvmStatic fun <T> filtered(msg: String = "filtered"): Result<T,Err> {
        return Failure<Err>(Codes.BAD_REQUEST, Codes.BAD_REQUEST.code, msg)
    }


    @JvmStatic fun <T> invalid(msg: String = "invalid"): Result<T,Err> {
        return Failure<Err>(Codes.BAD_REQUEST, Codes.BAD_REQUEST.code, msg)
    }


    @JvmStatic fun <T> failure(msg:String = "failed"): Result<T,Err> {
        return Failure(Codes.FAILURE, Codes.FAILURE.code, Codes.FAILURE.msg)
    }


    @JvmStatic fun <T> exception(err:Err): Result<T,Err> {
        return Failure(err, err.code, err.msg)
    }


    @JvmStatic fun <T> exception(ex:Exception): Result<T,Exception> {
        return Failure(ex, Codes.UNEXPECTED.code, ex.message ?: "")
    }
}
