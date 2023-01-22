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

sealed class LogLevel(val name: String, val code: Int) {

    operator fun compareTo(lv: LogLevel): Int = this.code.compareTo(lv.code)

    object Debug : LogLevel("Debug", 1)
    object Info  : LogLevel("Info", 2 )
    object Warn  : LogLevel("Warn", 3 )
    object Error : LogLevel("Error", 4)
    object Fatal : LogLevel("Fatal", 5)


    companion object {
        fun parse(level: String): LogLevel {
            return when (level.trim().lowercase()) {
                Debug.name -> Debug
                Info.name  -> Info
                Warn.name  -> Warn
                Error.name -> Error
                Fatal.name -> Fatal
                else -> Debug
            }
        }
    }
}