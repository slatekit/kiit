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

package slatekit.common.log

interface LogSupport {

    val logger: LoggerBase?


    /**
     * Logs an debug message
     * @param msg : The message
     * @param ex : The exception to log
     */
    fun debug(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Debug, msg, ex, tag)
    }


    /**
     * Logs an info message
     * @param msg : The message
     * @param ex : The exception to log
     */
    fun info(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Info, msg, ex, tag)
    }


    /**
     * Logs an warning
     * @param msg : The message
     * @param ex : The exception to log
     */
    fun warn(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Warn, msg, ex, tag)
    }


    /**
     * Logs an error
     * @param msg : The message
     * @param ex : The exception to log
     */
    fun error(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Error, msg, ex, tag)
    }


    /**
     * Logs an fatal
     *
     * @param msg : The message
     * @param ex : The exception to log
     */
    fun fatal(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Fatal, msg, ex, tag)
    }


    /**
     * Logs an entry
     * @param level
     * @param msg
     * @param ex
     */
    fun log(level: LogLevel, msg: String, ex: Exception? = null, tag: String? = null) {
        logger?.let { l -> l.log(level, msg, ex, tag) }
    }
}
