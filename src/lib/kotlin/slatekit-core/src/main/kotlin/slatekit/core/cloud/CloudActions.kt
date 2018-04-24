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

package slatekit.core.cloud

import slatekit.common.Failure
import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.results.ResultFuncs.successOrError

interface CloudActions {

    fun <T> execute(source: String, action: String, tag: String = "", audit: Boolean = false,
                    rethrow: Boolean = false, data: Any?, call: () -> T): T? {
        val result: T? = try {
            call()
        }
        catch(ex: Exception) {
            onError(source, action, tag, data, ex)
            null
        }
        return result
    }


    fun <T> executeResult(source: String,
                          action: String,
                          tag: String = "",
                          audit: Boolean = false,
                          data: Any?,
                          call: () -> T): ResultEx<T> {
        val result = try {
            val resultValue = call()
            Success(resultValue)
        }
        catch (ex: Exception) {
            onError(source, action, tag, data, ex)
            Failure(ex, msg = "Error performing action $action on $source with tag $tag. $ex")
        }
        return result
    }


    fun onAudit(source: String, action: String, tag: String, data: Any?) {
    }


    fun onError(source: String, action: String, tag: String, data: Any?, ex: Exception?) {
    }


    fun onWarn(source: String, action: String, tag: String, data: Any?, ex: Exception?) {
    }
}
