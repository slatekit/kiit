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

import slatekit.common.DateTime
import slatekit.common.args.Args
import slatekit.common.ext.durationFrom
import slatekit.common.functions.Function
import slatekit.common.functions.FunctionCalls
import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.FunctionMode
import slatekit.results.*
import java.util.concurrent.atomic.AtomicReference

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
open class Command(
        functionInfo: FunctionInfo,
        val call: ((CommandRequest) -> Any?)? = null
) : Function, FunctionCalls<CommandResult> {

    /**
     * Initialize the command info with just name and optional description
     */
    constructor(name: String, desc: String? = null) : this(FunctionInfo(name, desc
            ?: ""))


    /**
     * Initialize the command info with just name and optional description
     */
    constructor(name: String, desc: String?, call: ((CommandRequest) -> Any?)? = null) : this(FunctionInfo(name, desc
            ?: ""), call)


    override val info = functionInfo


    /**
     * Stores the last result
     */
    private val lastResult = AtomicReference<CommandResult>(CommandResult.empty(functionInfo))


    /**
     * Stores the last status
     */
    private val lastStatus = AtomicReference<CommandState>(CommandState.empty(functionInfo))


    /**
     * Expose the immutable last execution result of this command
     * @return
     */
    fun lastResult(): CommandResult = lastResult.get()

    /**
     * Expose the last known status of this command
     * @return
     */
    fun lastStatus(): CommandState = lastStatus.get()


    /**
     * execute this function with the supplied args
     *
     * @param args
     * @return
     */
    override fun execute(args: Array<String>, mode: FunctionMode) {
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
        val result = Try.attempt {
            Success(args)
                    .map { args -> convert(args) }
                    .map { request -> perform(request, mode) }
                    .map { result -> track(result) }
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
        val result = try {
            val rawValue = call?.invoke(request) ?: execute(request)
            val finalValue = when (rawValue) {
                is Success<*> -> rawValue.value
                else -> rawValue
            }
            Success(finalValue)
        } catch (ex: Exception) {
            Failure(buildError(ex), StatusCodes.UNEXPECTED)
        }
        val end = DateTime.now()
        val duration = end.durationFrom(start).toMillis()
        return CommandResult(request, info, mode, result, start, end, duration)
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


    /**
     * track the result internally, always storing the last result
     * @param result
     * @return
     */
    protected open fun track(result: CommandResult): CommandResult {
        val last = lastStatus()
        val curr = last.update(result)
        lastStatus.set(curr)
        return result
    }


    private fun buildError(ex: Exception): Exception {
        return Exception("Error while executing : ${info.name}. ${ex.message}", ex)
    }
}
