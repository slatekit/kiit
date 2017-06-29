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

package slatekit.apis

import slatekit.apis.core.Action
import slatekit.common.ListMap
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.Strings
import slatekit.common.results.ResultFuncs.failureWithCode
import slatekit.common.results.UNEXPECTED_ERROR
import slatekit.core.common.AppContext
import java.io.File


/**
 * Base class for any Api, provides lookup functionality to check for exposed api actions.
 * @param context   : The context of the application ( logger, config, encryptor, etc )
 */
open class ApiBase(val context: AppContext) {

    val isErrorEnabled = false
    var _lookup = ListMap<String, Action>()


    /**
     * hook to allow api to initialize itself
     */
    fun init(): Unit {}


    /**
     * gets a list of all the actions / methods this api supports.
     *
     * @return
     */
    fun actions(): ListMap<String, Action> = _lookup.clone()


    /**
     * whether or not the action exists in this api
     *
     * @param action : e.g. "invite" as in "users.invite"
     * @return
     */
    fun contains(action: String): Boolean = _lookup.contains(action)


    /**
     * gets the value with the supplied key(action)
     *
     * @param action
     * @return
     */
    operator fun get(action: String): Action? {
        return if (!contains(action))
            null
        else
            _lookup[action]!!
    }


    /**
     * gets the value with the supplied key(action)
     *
     * @param action
     * @return
     */
    fun getOpt(action: String): Action? = _lookup[action]


    /**
     * adds a key/value to this collection
     *
     * @param action
     * @param value
     */
    fun update(action: String, value: Action): Unit {
        _lookup = _lookup.add(action, value)
    }


    fun onException(context: AppContext, request: Request, ex: Exception): Result<Any> {
        return failureWithCode(UNEXPECTED_ERROR, msg = "unexpected error in api", err = ex)
    }


    protected fun interpretUri(path: String): String? {
        val pathParts = Strings.substring(path, "://")
        return pathParts?.let { parts ->
            val uri = parts.first
            val loc = parts.second
            this.context.dirs?.let { dirs ->
                when (uri) {
                    "user://"    -> File(System.getProperty("user.home"), loc).toString()
                    "temp://"    -> File(System.getProperty("java.io.tmpdir"), loc).toString()
                    "file://"    -> File(loc).toString()
                    "inputs://"  -> File(dirs.pathToInputs, loc).toString()
                    "outputs://" -> File(dirs.pathToOutputs, loc).toString()
                    "logs://"    -> File(dirs.pathToLogs, loc).toString()
                    "cache://"   -> File(dirs.pathToCache, loc).toString()
                    else         -> path
                }
            }
        }
    }


    protected fun writeToFile(msg: Any?, fileNameLocal: String, count: Int,
                              contentFetcher: (Any?) -> String): String? {
        return msg?.let { item ->
            val finalFileName = if (count == 0) fileNameLocal else fileNameLocal + "_" + count
            val path = interpretUri(finalFileName)
            val content = contentFetcher(msg)
            File(path.toString()).writeText(content)
            "File content written to : " + path
        } ?: "No items available"
    }
}
