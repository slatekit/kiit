package slatekit.providers.logback

import slatekit.common.log.*

class LogbackLogger(private val instance: org.slf4j.Logger) : Logger(parseLevel(instance), instance.name) {

    override val raw: Any? = instance

    /**
     * Logs to logback.
     * NOTE: Logback logger doesn't seem to have a .log method so
     * check log level and log to corresponding method
     *
     * @param entry: 
     */
    override fun log(entry: LogEntry) {
        when (level) {
            LogLevel.Debug -> instance.debug(entry.msg, entry.ex)
            LogLevel.Info  -> instance.info (entry.msg, entry.ex)
            LogLevel.Warn  -> instance.warn (entry.msg, entry.ex)
            LogLevel.Error -> instance.error(entry.msg, entry.ex)
            LogLevel.Fatal -> instance.error(entry.msg, entry.ex)
        }
    }

    companion object {
        fun parseLevel(instance: org.slf4j.Logger): LogLevel {
            return if (instance.isDebugEnabled) LogLevel.Debug
            else if (instance.isInfoEnabled)    LogLevel.Info
            else if (instance.isWarnEnabled)    LogLevel.Warn
            else if (instance.isErrorEnabled)   LogLevel.Error
            else LogLevel.Info
        }
    }
}
