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

package slatekit.cli

import slatekit.common.io.Files
import slatekit.common.args.ArgsFuncs
import slatekit.common.info.Folders
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.StatusCodes
import slatekit.results.Success

object CliUtils {

    fun log(folders: Folders, cmd: CliCommand, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun log(folders: Folders, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun checkForAssistance(cmd: CliCommand): Notice<Boolean> {
        val words = cmd.args.raw
        val verbs = cmd.args.actionParts
        return with(ArgsFuncs) {
            // Case 1: Exit ?
            if (isExit(words, 0)) {
                Success(true,"exit", StatusCodes.EXIT.code)
            }
            // Case 2a: version ?
            else if (isVersion(words, 0)) {
                Success(true,"version", StatusCodes.HELP.code)
            }
            // Case 2b: about ?
            else if (isAbout(words, 0)) {
                Success(true,"about", StatusCodes.HELP.code)
            }
            // Case 3a: Help ?
            else if (isHelp(words, 0)) {
                Success(true,"help", StatusCodes.HELP.code)
            }
            // Case 3b: Help on area ?
            else if (isHelp(verbs, 1)) {
                Success(true,"area ?", StatusCodes.HELP.code)
            }
            // Case 3c: Help on api ?
            else if (isHelp(verbs, 2)) {
                Success(true,"area.api ?", StatusCodes.HELP.code)
            }
            // Case 3d: Help on action ?
            else if (!cmd.args.action.isNullOrEmpty() &&
                    (isHelp(cmd.args.positional, 0) ||
                            isHelp(verbs, 3))
            ) {
                Success(true, "area.api.action ?", StatusCodes.HELP.code)
            } else
                Failure("")
        }
    }
}
