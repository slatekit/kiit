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

    val logger: Logger?

    /**
     * Logs an debug message
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun debug(msg: String, ex: Exception? = null) {
        log(LogLevel.Debug, msg, ex)
    }

    /**
     * Logs an info message
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun info(msg: String, ex: Exception? = null) {
        log(LogLevel.Info, msg, ex)
    }

    /**
     * Logs an warning
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun warn(msg: String, ex: Exception? = null) {
        log(LogLevel.Warn, msg, ex)
    }

    /**
     * Logs an error
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun error(msg: String, ex: Exception? = null) {
        log(LogLevel.Error, msg, ex)
    }

    /**
     * Logs an fatal
     *
     * @param msg : The message
     * @param ex : The exception to log
     */
    @Ignore
    fun fatal(msg: String, ex: Exception? = null) {
        log(LogLevel.Fatal, msg, ex)
    }

    /**
     * Logs an debug message
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun debug(callback: () -> String, ex: Exception? = null) {
        log(LogLevel.Debug, callback, ex)
    }

    /**
     * Logs an info message
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun info(callback: () -> String, ex: Exception? = null) {
        log(LogLevel.Info, callback, ex)
    }

    /**
     * Logs an warning
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun warn(callback: () -> String, ex: Exception? = null) {
        log(LogLevel.Warn, callback, ex)
    }

    /**
     * Logs an error
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun error(callback: () -> String, ex: Exception? = null) {
        log(LogLevel.Error, callback, ex)
    }

    /**
     * Logs an fatal
     *
     * @param msg : The callback to build the message
     * @param ex : The exception to log
     */
    @Ignore
    fun fatal(callback: () -> String, ex: Exception? = null) {
        log(LogLevel.Fatal, callback, ex)
    }

    /**
     * Logs an entry
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    fun log(level: LogLevel, msg: String, ex: Exception? = null) {
        logger?.let { l -> l.log(level, msg, ex) }
    }

    /**
     * Logs key/value pairs
     */
    @Ignore
    fun log(level: LogLevel, msg: String, pairs:List<Pair<String,String>>, ex:Exception? = null) {
        val info = pairs.joinToString { it -> it.first + "=" + it.second }
        logger?.let { l -> l.log(level, msg + " " + info, ex) }
    }


    @Ignore
    fun log(level: LogLevel, callback: () -> String, ex: Exception?) {
        logger?.let { l -> l.log(level, callback, ex) }
    }
}
