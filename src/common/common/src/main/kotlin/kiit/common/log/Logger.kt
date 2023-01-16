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

package kiit.common.log

import kiit.common.Ignore

abstract class Logger(
    open val level: LogLevel = LogLevel.Warn,
    open val name: String = "",
    open val logType: Class<*>? = null
) : LogSupport {

    fun isEnabled(level: LogLevel): Boolean = level >= this.level

    override val logger: Logger? by lazy { this }
    open val raw: Any? = null

    /**
     * Logs an entry
     *
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    fun performLog(level: LogLevel, msg: String?, ex: Throwable?) {
        if(level >= this.level) {
            log(LogEntry(name, level, msg ?: "", ex))
        }
    }

    /**
     * Logs an entry
     *
     * @param level
     * @param ex
     */
    @Ignore
    fun performLog(level: LogLevel, msg:String?, callback: () -> String) {
        if(level >= this.level) {
            val label = msg ?: ""
            val output = callback()
            log(LogEntry(name, level, "$label : $output"))
        }
    }

    abstract fun log(entry: LogEntry)
}
