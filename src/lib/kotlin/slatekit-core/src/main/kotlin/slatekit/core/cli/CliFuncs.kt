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

import slatekit.common.Files
import slatekit.common.Result
import slatekit.common.args.ArgsFuncs
import slatekit.common.info.Folders
import slatekit.common.results.EXIT
import slatekit.common.results.HELP
import slatekit.common.results.ResultFuncs.no
import slatekit.common.results.ResultFuncs.yesWithCode


object CliFuncs {

    fun log(folders: Folders, cmd: CliCommand, content: String): Unit {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }


    fun log(folders: Folders, content: String): Unit {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }


    fun checkForAssistance(cmd: CliCommand): Result<Boolean> {
        val words = cmd.args.raw
        val verbs = cmd.args.actionVerbs

        // Case 1: Exit ?
        return if (ArgsFuncs.isExit(words, 0)) {
            yesWithCode(EXIT, msg = "exit", tag = cmd.args.action)
        }
        // Case 2a: version ?
        else if (ArgsFuncs.isVersion(words, 0)) {
            yesWithCode(HELP, msg = "version", tag = cmd.args.action)
        }
        // Case 2b: about ?
        else if (ArgsFuncs.isAbout(words, 0)) {
            yesWithCode(HELP, msg = "about", tag = cmd.args.action)
        }
        // Case 3a: Help ?
        else if (ArgsFuncs.isHelp(words, 0)) {
            yesWithCode(HELP, msg = "help", tag = cmd.args.action)
        }
        // Case 3b: Help on area ?
        else if (ArgsFuncs.isHelp(verbs, 1)) {
            yesWithCode(HELP, msg = "area ?", tag = cmd.args.action)
        }
        // Case 3c: Help on api ?
        else if (ArgsFuncs.isHelp(verbs, 2)) {
            yesWithCode(HELP, msg = "area.api ?", tag = cmd.args.action)
        }
        // Case 3d: Help on action ?
        else if (!cmd.args.action.isNullOrEmpty() &&
                (ArgsFuncs.isHelp(cmd.args.positional, 0) ||
                        ArgsFuncs.isHelp(verbs, 3))
                     ) {
            yesWithCode(HELP, msg = "area.api.action ?", tag = cmd.args.action)
        }
        else
            no()
    }
}
