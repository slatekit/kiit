package slatekit.common.log

import slatekit.common.Internal

@Internal("NON-PRODUCTION USAGE: Static logger for internal use, unit-tests, prototyping only")
object Log : Logger() {

    private var _internalLogger: Logger? = null

    override val logger : Logger? by lazy { _internalLogger }
    override val level  : LogLevel  get() { return _internalLogger?.level ?: LogLevel.Debug }
    override val name   : String    get() { return _internalLogger?.name  ?: "slatekit" }
    override val logType: Class<*>? get() { return _internalLogger?.logType }

    /**
     * Initialize with actual logger
     */
    fun init(logger: Logger) {
        _internalLogger = logger
    }


    /**
     * Delegate to internal logger
     */
    override fun log(entry: LogEntry) {
        _internalLogger?.let {
            it.log(entry)
        }
    }

}