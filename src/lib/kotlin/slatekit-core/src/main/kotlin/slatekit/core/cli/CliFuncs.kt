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

import slatekit.common.io.Files
import slatekit.common.args.ArgsFuncs
import slatekit.common.info.Folders
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.StatusCodes
import slatekit.results.Success

object CliFuncs {

    fun log(folders: Folders, cmd: CliCommand, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun log(folders: Folders, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun checkForAssistance(cmd: CliCommand): Notice<Boolean> {
        val words = cmd.args.raw
        val verbs = cmd.args.actionParts

        // Case 1: Exit ?
        return if (ArgsFuncs.isExit(words, 0)) {
            Success(true,"exit", StatusCodes.EXIT.code)
        }
        // Case 2a: version ?
        else if (ArgsFuncs.isVersion(words, 0)) {
            Success(true,"version", StatusCodes.HELP.code)
        }
        // Case 2b: about ?
        else if (ArgsFuncs.isAbout(words, 0)) {
            Success(true,"about", StatusCodes.HELP.code)
        }
        // Case 3a: Help ?
        else if (ArgsFuncs.isHelp(words, 0)) {
            Success(true,"help", StatusCodes.HELP.code)
        }
        // Case 3b: Help on area ?
        else if (ArgsFuncs.isHelp(verbs, 1)) {
            Success(true,"area ?", StatusCodes.HELP.code)
        }
        // Case 3c: Help on api ?
        else if (ArgsFuncs.isHelp(verbs, 2)) {
            Success(true,"area.api ?", StatusCodes.HELP.code)
        }
        // Case 3d: Help on action ?
        else if (!cmd.args.action.isNullOrEmpty() &&
                (ArgsFuncs.isHelp(cmd.args.positional, 0) ||
                        ArgsFuncs.isHelp(verbs, 3))
                     ) {
            Success(true, "area.api.action ?", StatusCodes.HELP.code)
        } else
            Failure("")
    }
}
