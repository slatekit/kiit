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

object Logger {

    fun parseLogLevel(level: String): LogLevel {
        return when (level) {
            "debug" -> Debug
            "info"  -> Info
            "warn"  -> Warn
            "error" -> Error
            "fatal" -> Fatal
            else    -> Debug
        }
    }
}
