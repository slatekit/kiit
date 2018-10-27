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

package slatekit.core.cli

object CliConstants {
    val BatchModeFailOnError = 0
    val BatchModeContinueOnError = 1
    val VerbPartArea = 1
    val VerbPartApi = 2
    val VerbPartAction = 3

    val EXIT = "exit"
    val VERSION = "version"
    val ABOUT = "about"
    val HELP = "help"
    val HELP_AREA = "area ?"
    val HELP_API = "area.api ?"
    val HELP_ACTION = "area.api.action ?"

    val SysSample = "sample"
    val SysFile = "file"
    val SysFormat = "format"
    val SysLog = "log"
    val SysCodeGen = "codegen"
}
