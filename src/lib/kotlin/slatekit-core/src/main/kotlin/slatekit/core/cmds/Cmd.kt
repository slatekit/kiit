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
import slatekit.results.Failure
import slatekit.results.StatusCodes
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries
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
open class Cmd(
    val name: String,
    val desc: String? = null,
    val call: ((Array<String>?) -> Any?)? = null
) {

    private val lastResult = AtomicReference<CmdResult>(CmdFuncs.defaultResult(name))
    private val lastStatus = AtomicReference<CmdState>(CmdFuncs.defaultState(name))

    /**
     * Expose the immutable last execution result of this command
     * @return
     */
    fun lastResult(): CmdResult = lastResult.get()

    /**
     * Expose the last known status of this command
     * @return
     */
    fun lastStatus(): CmdState = lastStatus.get()

    /**
     * execute this command with optional arguments
     *
     * @param args
     * @return
     */
    fun execute(args: Array<String>? = null): CmdResult {
        // Track time
        val start = DateTime.now()

        // Result
        val result: Try<Any> =
                try {

                    call?.let { c ->
                        val res = c(args)
                        val finalResult = when(res){
                            null -> Failure("unable to execute command")
                            else -> Success(res)
                        }
                        finalResult.toTry()
                    } ?: executeInternal(args)
                } catch (ex: Exception) {
                    Tries.unexpected<Any>(Exception("Error while executing : " + name + ". " + ex.message, ex))
                }

        // Stop tracking time (inclusive of possible error )
        val end = DateTime.now()

        // The result
        val cmdResult = CmdFuncs.fromResult(name, start, end, result)

        // Track the last result and build updated status
        lastResult.set(cmdResult)
        lastStatus.set(lastStatus.get().update(cmdResult))

        return cmdResult
    }

    /**
     * executes the command, this should be overridden in sub-classes
     *
     * @param args
     * @return
     */
    protected open fun executeInternal(args: Array<String>?): Try<Any> =
            Failure<Any>(StatusCodes.UNIMPLEMENTED).toTry()
}
