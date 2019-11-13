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

import slatekit.common.Ignore

abstract class Logger(
    val level: LogLevel = LogLevel.Warn,
    val name: String = "",
    val logType: Class<*>? = null
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
    override fun log(level: LogLevel, msg: String, ex: Exception?) {
        checkLog(level) {
            performLog(LogEntry(name, level, msg, ex))
        }
    }

    /**
     * Logs an entry
     *
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    override fun log(level: LogLevel, msg: String, pairs:List<Pair<String,String>>, ex: Exception?) {
        checkLog(level) {
            val info = pairs.joinToString { it -> it.first + "=" + it.second }
            performLog(LogEntry(name, level, "$msg $info", null))
        }
    }

    /**
     * Logs an entry
     *
     * @param level
     * @param ex
     */
    @Ignore
    override fun log(level: LogLevel, callback: () -> String, ex: Exception?) {
        checkLog(level) {
            val msg = callback()
            performLog(LogEntry(name, level, msg, ex))
        }
    }

    private fun checkLog(level: LogLevel, callback: () -> Unit) {
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
