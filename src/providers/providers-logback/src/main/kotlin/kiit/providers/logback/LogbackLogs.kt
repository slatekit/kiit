package slatekit.providers.logback

import slatekit.common.log.Logger
import slatekit.common.log.Logs

import org.slf4j.LoggerFactory

class LogbackLogs : Logs {
    override val provider: Any = "logback"

    override fun getLogger(name: String?): Logger {
        val instance = LoggerFactory.getLogger(name ?: "")
       return LogbackLogger(instance)
    }

    override fun getLogger(cls: Class<*>): Logger {
        val instance = LoggerFactory.getLogger(cls)
        return LogbackLogger(instance)
    }
}
