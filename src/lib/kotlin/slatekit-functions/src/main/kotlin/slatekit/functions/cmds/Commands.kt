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

package slatekit.functions.cmds

import slatekit.common.args.Args
import slatekit.functions.common.*
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries
import slatekit.results.getOrElse

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 * @param all
 */
class Commands(override val all: List<Command>) : Functions<Command> {
    private val states = all.map { it.name to CommandState.empty(it.info) }.toMap()

    /**
     * Runs the command with the supplied name
     * @param name
     * @return
     */
    fun run(name: String, args: Array<String>? = null): Outcome<CommandResult> {
        val command = getOrNull(name)
        val result = when(command) {
            null -> Outcomes.errored<CommandResult>("$name not found")
            else -> {
                val parseResult = Args.parseArgs(args ?: arrayOf())
                when (parseResult) {
                    is Failure<Exception> -> Outcomes.errored(parseResult.error)
                    is Success<Args> -> command.execute(parseResult.value, FunctionMode.Interacted).toOutcome()
                }
            }
        }
        return result
    }


    /**
     * Gets the state of the command with the supplied name
     * @param name
     * @return
     */
    fun state(name: String): Outcome<CommandState> {
        val command = getOrNull(name)
        val result = when(command) {
            null -> Outcomes.errored<CommandState>("Command $name not found")
            else -> Outcomes.success(states.get(command.name) ?: CommandState.empty(command.info))
        }
        return result
    }
}
