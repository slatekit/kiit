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

import slatekit.common.DateTime

abstract class LoggerBase(val level: LogLevel = Warn,
                          val name: String = "",
                          val logType: Class<Any>? = null) : LogSupport {
    init {

    }


    /**
     * gets a  instance of logger with the supplied type, name and level
     * with the same level as this one by default
     * @param t
     * @param name
     * @param lvl
     * @return
     */
    fun getLogger(lvl: LogLevel, name: String, t: Class<Any>? = null): LoggerBase {
        return LoggerConsole(lvl, name, t)
    }


    /**
     * Logs an entry
     *
     * @param level
     * @param msg
     * @param ex
     */
    override fun log(level: LogLevel, msg: String, ex: Exception?, tag: String?) {
        checkLog(level, {
            performLog(buildLog(level, msg, ex, tag))
        })
    }


    /**
     * Logs an entry
     */
    fun log(entry: LogEntry) {
        checkLog(level, {
            performLog(entry)
        })
    }


    /**
     * Logs an entry
     *
     * @param level
     * @param msg
     * @param ex
     */
    protected fun buildLog(level: LogLevel, msg: String, ex: Exception? = null, tag: String? = null): LogEntry {
        return LogEntry(name, level, msg, DateTime.now(), ex, tag)
    }


    protected fun checkLog(level: LogLevel, callback: () -> Unit): Unit {
        if (level >= this.level) {
            callback()
        }
    }


    /**
     * Logs an entry
     *
     * @param entry
     */
    protected abstract fun performLog(entry: LogEntry): Unit
}
