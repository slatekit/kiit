/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.common.log

object LogHelper {

    fun parseLevel(level: String): LogLevel {
        return when (level.trim().toLowerCase()) {
            "debug" -> Debug
            "info"  -> Info
            "warn"  -> Warn
            "error" -> Error
            "fatal" -> Fatal
            else    -> Debug
        }
    }
}
