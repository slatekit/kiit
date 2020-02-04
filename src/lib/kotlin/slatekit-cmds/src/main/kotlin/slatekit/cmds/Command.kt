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

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.envs.EnvMode
import slatekit.policy.common.Function
import slatekit.policy.common.*
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

/**
 * Light-weight implementation of a command-like pattern that can execute some code
 * and track the last result of that code.
 *
 * NOTES:
 * 1. The code to execute can be a function that you pass in
 * 2. You can also derive this class and override the executeInternal method
 * 3. The result (CmdResult) of an execution is stored in the lastResult
 * 4. The result has the following info
 *
 *    - name of the command
 *    - success/failure of the command
 *    - message of success/failure
 *    - result of the last command
 *    - time started
 *    - time ended
 *    - duration of the execution
 *
 * The commands can be registered with the Cmds component and you track the last time
 * a command was run.
 * @param name
 */
open class Command(override val id: Identity,
                   val desc: String,
                   val call: ((CommandRequest) -> Any?)? = null,
                   val schema: ArgsSchema = ArgsSchema.empty) : Function {

    /**
     * Initialize the command info with just name and optional description
     */
    constructor(name: String, desc: String? = null, env: EnvMode = EnvMode.Dev, call: ((CommandRequest) -> Any?)? = null)
            : this(Identity.cmd(name, env), desc ?: "", call)


    /**
     * execute this function with the supplied args
     *
     * @param args
     * @return
     */
    fun execute(args: Array<String>) {
        execute(args, FunctionMode.Called)
    }


    /**
     * execute this function with the supplied args
     *
     * @param args
     * @return
     */
    fun execute(args: Array<String>, mode: FunctionMode) {
        val parseResult = Args.parseArgs(args)
        when (parseResult) {
            is Failure<Exception> -> Failure(parseResult.error)
            is Success<Args> -> execute(parseResult.value, mode)
        }
    }


    /**
     * execute this command with optional arguments
     *
     * @param args
     * @return
     */
    fun execute(args: Args, mode: FunctionMode): Try<CommandResult> {
        val result = Tries.of {
            Success(args)
                    .map { args -> convert(args) }
                    .map { request -> perform(request, mode) }
                    .map { result -> handle(result) }
        }
        return result.inner()
    }


    /**
     * Converts the parsed arguments into a command request
     */
    protected open fun convert(args: Args): CommandRequest {
        return CommandRequest(args)
    }


    /**
     * executes the command, this should be overridden in sub-classes
     *
     * @param request
     * @return
     */
    fun perform(request: CommandRequest, mode: FunctionMode): CommandResult {
        val start = DateTime.now()
        val result:Outcome<Any?> = try {
            val rawValue = call?.invoke(request) ?: execute(request)
            val finalValue = when (rawValue) {
                is Success<*> -> rawValue.value
                else -> rawValue
            }
            Outcomes.success(finalValue)
        } catch (ex: Exception) {
            Outcomes.errored(ex)
        }
        val end = DateTime.now()
        return CommandResult(request, mode, result, start, end)
    }


    /**
     * executes the command, this should be overridden in sub-classes
     *
     * @param request
     * @return
     */
    protected open fun execute(request: CommandRequest): Any {
        return true
    }


    /**
     * handles the result of the command. this is a workflow hook for derived classes
     * this could be where you add diagnostics
     * @param result
     * @return
     */
    protected open fun handle(result: CommandResult): CommandResult {
        return result
    }
}
