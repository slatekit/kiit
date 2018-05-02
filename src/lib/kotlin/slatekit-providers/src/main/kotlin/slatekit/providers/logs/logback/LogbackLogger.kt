package slatekit.providers.logs.logback

import slatekit.common.log.*

class LogbackLogger(private val instance:org.slf4j.Logger) : Logger(parseLevel(instance), instance.name) {

    override val raw   : Any? = instance

    /**
     * Logs to logback.
     * NOTE: Logback logger doesn't seem to have a .log method so
     * check log level and log to corresponding method
     *
     * @param entry :
     */
    override fun performLog(entry: LogEntry) {
        when (entry.level) {
            Debug -> instance.debug(entry.msg, entry.ex)
            Info  -> instance.info(entry.msg, entry.ex)
            Warn  -> instance.warn(entry.msg, entry.ex)
            Error -> instance.error(entry.msg, entry.ex)
            Fatal -> instance.error(entry.msg, entry.ex)
            else  -> instance.debug(entry.msg, entry.ex)
        }
    }


    companion object {
        fun parseLevel(instance: org.slf4j.Logger): LogLevel {
            return if(instance.isDebugEnabled) Debug
            else if(instance.isInfoEnabled) Info
            else if(instance.isWarnEnabled) Warn
            else if(instance.isErrorEnabled) Error
            else Info
        }
    }
}
