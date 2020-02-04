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

package slatekit.cmds

import slatekit.common.Status
import slatekit.common.args.Args
import slatekit.policy.common.*
import slatekit.results.*
import slatekit.results.builders.Outcomes

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 * @param all
 */
class Commands(override val all: List<Command>) : Functions<Command> {
    private val states = all.map { it.id.name to CommandState.empty(it.id) }.toMap().toMutableMap()

    /**
     * Runs the command with the supplied name
     * @param name
     * @return
     */
    fun run(name: String, args: Array<String>? = null): Outcome<CommandResult> {
        val command = getOrNull(name)
        val outcome = when(command) {
            null -> Outcomes.errored("$name not found")
            else -> {
                val parseResult = Args.parseArgs(args ?: arrayOf())
                val r = when (parseResult) {
                    is Failure<Exception> -> Outcomes.errored(parseResult.error)
                    is Success<Args> -> command.execute(parseResult.value, FunctionMode.Interacted).toOutcome()
                }
                r
            }
        }
        command?.let { cmd ->
            when (outcome) {
                is Success -> {
                    states[name] = CommandState(cmd.id, Status.Running, outcome)
                }
                is Failure -> {
                    states[name] = CommandState(cmd.id, Status.Failed, outcome)
                }
            }
        }
        return outcome
    }


    /**
     * Gets the state of the command with the supplied name
     * @param name
     * @return
     */
    fun state(name: String): Outcome<CommandState> {
        val command = getOrNull(name)
        val result = when(command) {
            null -> Outcomes.errored("Command $name not found")
            else -> Outcomes.success(states.get(command.id.name) ?: CommandState.empty(command.id))
        }
        return result
    }
}
