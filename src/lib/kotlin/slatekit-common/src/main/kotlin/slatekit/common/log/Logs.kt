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


interface Logs {

    fun getLogger(name: String? = ""): Logger
    fun getLogger(cls: Class<*>): Logger
}

/**
 * Simple console logger as a default.
 * Use slatekit.providers.logs.LogbackLogs as provider for LogBack
 *
 * slateKit.common has only 0 dependencies!!!
 */
object LogsDefault : Logs {

    override fun getLogger(cls: Class<*>): Logger {
        return LoggerConsole(name = cls.simpleName, logType = cls)
    }

    override fun getLogger(name: String?): Logger {
        return LoggerConsole(name = name ?: "console")
    }
}
