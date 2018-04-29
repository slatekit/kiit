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

import slatekit.common.console.ConsoleWriter


/**
 * Lightweight logger that prints to the console
 * using the ConsoleWriter in slate kit which has support
 * for colors and text semantics.
 * This is just used mostly for defaults.
 * You should be using the getLogger method to auto load
 * a more robust logger like log4j2 ( see examples )
 */
class LoggerConsole(level: LogLevel = Debug,
                    name: String = "console",
                    logType: Class<Any>? = null) : LoggerBase(level, name, logType) {

    private val _writer = ConsoleWriter()

    override val logger: LoggerBase get() = this


    /**
     * Logs to the console
     *
     * @param entry :
     */
    override fun performLog(entry: LogEntry) {
        when (entry.level) {
            Debug -> _writer.subTitle(entry.level.name + " : " + entry.msg)
            Info  -> _writer.text(entry.level.name + "  : " + entry.msg)
            Warn  -> _writer.url(entry.level.name + "  : " + entry.msg)
            Error -> _writer.error(entry.level.name + " : " + entry.msg)
            Fatal -> _writer.highlight(entry.level.name + " : " + entry.msg)
            else  -> _writer.text(entry.level.name + " : " + entry.msg)
        }
    }
}
