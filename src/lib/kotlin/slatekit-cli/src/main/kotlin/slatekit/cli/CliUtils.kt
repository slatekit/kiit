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

import slatekit.common.args.Args
import slatekit.common.io.Files
import slatekit.common.args.ArgsFuncs
import slatekit.common.info.Folders
import slatekit.results.*

object CliUtils {

    fun log(folders: Folders, req: CliRequest, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun log(folders: Folders, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }


    fun convert(line: String, prefix:String, separator:String): Try<CliRequest> {
        // 1st step, parse the command line into arguments
        val argsResult = Args.parse(line, prefix, separator, true)
        return Success(CliRequest.build((argsResult as Success<Args>).value, line))
    }

    fun checkForAssistance(req: CliRequest): Notice<Boolean> {
        val words = req.args.raw
        val verbs = req.args.actionParts

        return with(ArgsFuncs) {
            // Case 1: Exit ?
            if (isExit(words, 0)) {
                Success(true, Command.Exit.id, StatusCodes.EXIT.code)
            }
            // Case 2a: version ?
            else if (isVersion(words, 0)) {
                Success(true,Command.Version.id, StatusCodes.HELP.code)
            }
            // Case 2b: about ?
            else if (isAbout(words, 0)) {
                Success(true,Command.About.id, StatusCodes.HELP.code)
            }
            // Case 3a: Help ?
            else if (isHelp(words, 0)) {
                Success(true,Command.Help.id, StatusCodes.HELP.code)
            } else
                Failure("")
        }
    }
}
