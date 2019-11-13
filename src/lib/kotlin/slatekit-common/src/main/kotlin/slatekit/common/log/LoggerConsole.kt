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

/**
 * Lightweight logger that prints to the console
 * using the ConsoleWriter in slate kit which has support
 * for colors and text semantics.
 * This is just used mostly for defaults.
 * You should be using the slatekit.providers module with support for logback
 */
class LoggerConsole(
    level: LogLevel = LogLevel.Debug,
    name: String = "console",
    logType: Class<*>? = null
) : Logger(level, name, logType) {

    /**
     * Logs to the console
     *
     * @param entry: 
     */
    override fun performLog(entry: LogEntry) {
        val prefix = "${entry.time} [$name] ${level.name}"
        when (entry.level) {
            LogLevel.Debug -> println("$prefix + : + ${entry.msg}")
            LogLevel.Info  -> println("$prefix + : + ${entry.msg}")
            LogLevel.Warn  -> println("$prefix + : + ${entry.msg}")
            LogLevel.Error -> println("$prefix + : + ${entry.msg}")
            LogLevel.Fatal -> println("$prefix + : + ${entry.msg}")
        }
    }
}
