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

package slatekit.core.cmds

import slatekit.common.args.Args
import slatekit.core.common.functions.FunctionInfo
import slatekit.core.common.functions.FunctionMode
import slatekit.core.common.functions.Functions
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.builders.Tries
import slatekit.results.getOrElse

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 * @param all
 */
class Commands(override val all: List<Command>) : Functions<Command> {

    /**
     * Runs the command with the supplied name
     * @param name
     * @return
     */
    fun run(name: String, args: Array<String>? = null): CommandResult {
        val command = getOrNull(name)
        val result = when(command) {
            null -> Tries.errored<CommandResult>("$name not found")
            else -> {
                val parseResult = Args.parseArgs(args ?: arrayOf())
                when (parseResult) {
                    is Failure<Exception> -> Failure(parseResult.error)
                    is Success<Args> -> command.execute(parseResult.value, FunctionMode.Interacted)
                }
            }
        }
        return result.getOrElse{ CommandResult.empty(FunctionInfo(name, "$name not found")) }
    }

    /**
     * Gets the state of the command with the supplied name
     * @param name
     * @return
     */
    fun state(name: String): CommandState {
        val command = getOrNull(name)
        val result = when(command) {
            null -> Tries.errored("Command $name not found")
            else -> Tries.success(command.lastStatus())
        }
        return result.getOrElse{ CommandState.empty(FunctionInfo(name, "")) }
    }
}
