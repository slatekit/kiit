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

import slatekit.common.utils.Files
import slatekit.common.ResultMsg
import slatekit.common.args.ArgsFuncs
import slatekit.common.info.Folders
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.HELP
import slatekit.common.results.ResultFuncs.no
import slatekit.common.results.ResultFuncs.yes

object CliFuncs {

    fun log(folders: Folders, cmd: CliCommand, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun log(folders: Folders, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun checkForAssistance(cmd: CliCommand): ResultMsg<Boolean> {
        val words = cmd.args.raw
        val verbs = cmd.args.actionParts

        // Case 1: Exit ?
        return if (ArgsFuncs.isExit(words, 0)) {
            yes("exit", EXIT)
        }
        // Case 2a: version ?
        else if (ArgsFuncs.isVersion(words, 0)) {
            yes("version", HELP)
        }
        // Case 2b: about ?
        else if (ArgsFuncs.isAbout(words, 0)) {
            yes("about", HELP)
        }
        // Case 3a: Help ?
        else if (ArgsFuncs.isHelp(words, 0)) {
            yes("help", HELP)
        }
        // Case 3b: Help on area ?
        else if (ArgsFuncs.isHelp(verbs, 1)) {
            yes("area ?", HELP)
        }
        // Case 3c: Help on api ?
        else if (ArgsFuncs.isHelp(verbs, 2)) {
            yes("area.api ?", HELP)
        }
        // Case 3d: Help on action ?
        else if (!cmd.args.action.isNullOrEmpty() &&
                (ArgsFuncs.isHelp(cmd.args.positional, 0) ||
                        ArgsFuncs.isHelp(verbs, 3))
                     ) {
            yes("area.api.action ?", HELP)
        } else
            no()
    }
}
