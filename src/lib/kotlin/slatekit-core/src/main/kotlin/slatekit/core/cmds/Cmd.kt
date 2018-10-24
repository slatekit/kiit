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

import slatekit.common.*
import slatekit.common.DateTime.Companion.now
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.notImplemented
import slatekit.common.results.ResultFuncs.successOrError
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.common.results.UNEXPECTED_ERROR
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
open class Cmd(val name: String,
               val desc: String? = null,
               call: ((Array<String>?) -> Any?)? = null) {

    private val _call = call
    private val _lastResult = AtomicReference<CmdResult>(CmdFuncs.defaultResult(name))
    private val _lastStatus = AtomicReference<CmdState>(CmdFuncs.defaultState(name))


    /**
     * Expose the immutable last execution result of this command
     * @return
     */
    fun lastResult(): CmdResult = _lastResult.get()


    /**
     * Expose the last known status of this command
     * @return
     */
    fun lastStatus(): CmdState = _lastStatus.get()


    /**
     * execute this command with optional arguments
     *
     * @param args
     * @return
     */
    fun execute(args: Array<String>? = null): CmdResult {
        // Track time
        val start = now()

        // Result
        val result: ResultEx<Any> =
                try {

                    _call?.let { c ->
                        val res = c(args)
                        successOrError(res != null, res).toResultEx()
                    } ?: executeInternal(args)
                }
                catch(ex: Exception) {
                    unexpectedError(ex, "Error while executing : " + name + ". " + ex.message)
                }


        // Stop tracking time (inclusive of possible error )
        val end = now()

        // The result
        val cmdResult = CmdFuncs.fromResult(name, start, end, result)

        // Track the last result and build updated status
        _lastResult.set(cmdResult)
        _lastStatus.set(_lastStatus.get().update(cmdResult))

        return cmdResult
    }


    /**
     * executes the command, this should be overridden in sub-classes
     *
     * @param args
     * @return
     */
    open protected fun executeInternal(args: Array<String>?): ResultEx<Any> =
            notImplemented<Any>().toResultEx()
}
