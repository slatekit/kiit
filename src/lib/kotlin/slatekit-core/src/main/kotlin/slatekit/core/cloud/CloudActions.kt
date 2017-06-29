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

import slatekit.common.Result
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
                          call: () -> T): Result<T> {
        val result = try {
            val resultValue = call()
            Triple(true, "", resultValue)
        }
        catch (ex: Exception) {
            onError(source, action, tag, data, ex)
            Triple(false, "Error performing action $action on $source with tag $tag. $ex", null)
        }

        val success = result.first
        val message = result.second
        val resData = result.third
        return successOrError(success, resData, message, tag)
    }


    fun onAudit(source: String, action: String, tag: String, data: Any?): Unit {
    }


    fun onError(source: String, action: String, tag: String, data: Any?, ex: Exception?): Unit {
    }


    fun onWarn(source: String, action: String, tag: String, data: Any?, ex: Exception?): Unit {
    }
}
