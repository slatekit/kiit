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

import slatekit.common.Ignore

/**
 * Log methods with messages that are both eager and lazyily called via functions
 */
interface LogSupport {

    val logger: LoggerBase?


    /**
     * Logs an debug message
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun debug(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Debug, msg, ex, tag)
    }


    /**
     * Logs an info message
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun info(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Info, msg, ex, tag)
    }


    /**
     * Logs an warning
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun warn(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Warn, msg, ex, tag)
    }


    /**
     * Logs an error
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun error(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Error, msg, ex, tag)
    }


    /**
     * Logs an fatal
     *
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun fatal(msg: String, ex: Exception? = null, tag: String? = null) {
        log(Fatal, msg, ex, tag)
    }


    /**
     * Logs an debug message
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun debug(callback: () -> String, ex: Exception? = null, tag: String? = null) {
        log(Debug, callback, ex, tag)
    }


    /**
     * Logs an info message
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun info(callback: () -> String, ex: Exception? = null, tag: String? = null) {
        log(Info, callback, ex, tag)
    }


    /**
     * Logs an warning
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun warn(callback: () -> String, ex: Exception? = null, tag: String? = null) {
        log(Warn, callback, ex, tag)
    }


    /**
     * Logs an error
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun error(callback: () -> String, ex: Exception? = null, tag: String? = null) {
        log(Error, callback, ex, tag)
    }


    /**
     * Logs an fatal
     *
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun fatal(callback: () -> String, ex: Exception? = null, tag: String? = null) {
        log(Fatal, callback, ex, tag)
    }


    /**
     * Logs an entry
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    fun log(level: LogLevel, msg: String, ex: Exception? = null, tag: String? = null) {
        logger?.let { l -> l.log(level, msg, ex, tag) }
    }


    @Ignore
    fun log(level: LogLevel, callback: () -> String, ex: Exception?, tag: String?) {
    }
}
