/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
  *  </kiit_header>
 */

package kiit.common.log

import kiit.common.Provider


interface Logs : Provider {

    fun getLogger(name: String? = ""): Logger
    fun getLogger(cls: Class<*>): Logger
}

/**
 * Simple console logger as a default.
 * Use kiit.providers.logs.LogbackLogs as provider for LogBack
 *
 * kiit.common has only 0 dependencies!!!
 */
object LogsDefault : Logs {

    /**
     * Can't return an singleton of Console
     */
    override val provider: Any = "console"

    override fun getLogger(cls: Class<*>): Logger {
        return LoggerConsole(name = cls.simpleName, logType = cls)
    }

    override fun getLogger(name: String?): Logger {
        return LoggerConsole(name = name ?: "console")
    }
}
