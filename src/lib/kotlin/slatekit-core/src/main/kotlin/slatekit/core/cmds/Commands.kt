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

import slatekit.core.common.FunctionInfo
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 * @param cmds
 */
class Commands(cmds: List<Command>) {

    /**
     * Create a lookup of command name to command
     */
    private val lookup = cmds.map { cmd -> cmd.name to cmd }.toMap()

    /**
     * names of commands
     */
    val names: List<String> = cmds.map { cmd -> cmd.name }

    /**
     * number of commands
     */
    val size: Int = lookup.size

    /**
     * whether or not there is a command with the supplied name.
     * @param name
     * @return
     */
    fun contains(name: String): Boolean = lookup.contains(name)

    /**
     * Runs the command with the supplied name
     * @param name
     * @return
     */
    fun run(name: String, args: Array<String>? = null): CommandResult {
        val command = getOrNull(name)
        val result = when(command) {
            null -> Tries.errored("Command $name not found")
            else -> command.execute(args)
        }
        return flatten(result, CommandResult.empty(FunctionInfo(name, "$name not found")))
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
        return flatten(result, CommandState.empty(FunctionInfo(name, "")))
    }


    private fun getOrNull(name:String):Command? {
        if(!contains(name)) {
            return null
        }
        return lookup[name]
    }


    private fun <T> flatten(result:Try<T>, default:T): T {
        return when(result){
            is Failure<*> -> default
            is Success<T> -> result.value
        }
    }
}
