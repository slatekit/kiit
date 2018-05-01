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
import slatekit.common.Ignore

abstract class Logger(val level: LogLevel = Warn,
                      val name: String = "",
                      val logType: Class<*>? = null) : LogSupport {


    fun isEnabled(level:LogLevel):Boolean = level >= this.level


    override val logger: Logger? by lazy { this }

    /**
     * Logs an entry
     *
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    override fun log(level: LogLevel, msg: String, ex: Exception?) {
        checkLog(level, {
            performLog(buildLog(level, msg, ex))
        })
    }


    /**
     * Logs an entry
     *
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    override fun log(level: LogLevel, callback: () -> String, ex: Exception?) {
        checkLog(level, {
            val msg = callback()
            performLog(buildLog(level, msg, ex))
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
    private fun buildLog(level: LogLevel, msg: String, ex: Exception? = null): LogEntry {
        return LogEntry(name, level, msg, DateTime.now(), ex)
    }


    private fun checkLog(level: LogLevel, callback: () -> Unit): Unit {
        if (level >= this.level) {
            callback()
        }
    }


    /**
     * Logs an entry
     *
     * @param entry
     */
    protected abstract fun performLog(entry: LogEntry)
}
