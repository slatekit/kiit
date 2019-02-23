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
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse

object CmdFuncs {

    /**
     * builds a default Command State
     * @param name
     * @return
     */
    fun defaultState(name: String): CmdState =
            CmdState(
                    name = name,
                    msg = "Not yet run",
                    lastRuntime = DateTime.MIN,
                    hasRun = false,
                    runCount = 0,
                    errorCount = 0,
                    lastResult = null
            )

    /**
     * Builds a default Command Result
     * @param name : The name of the command
     * @return
     */
    fun defaultResult(name: String): CmdResult =

            // The result
            CmdResult(
                    name = name,
                    result = Success("default"),
                    started = DateTime.MIN,
                    ended = DateTime.MIN,
                    totalMs = 0
            )

    /**
     * builds an error CommandState
     * @param name
     * @param message
     * @return
     */
    fun errorState(name: String, message: String): CmdState =
            CmdState(name, message, DateTime.MIN, false, 0, 0, null)

    /**
     * builds an error Command Result
     * @param name
     * @param message
     * @return
     */
    fun errorResult(name: String, message: String): CmdResult =
            CmdResult(name, Failure<Any>(message),  now(), now(), 0)

    /**
     * Converts an Tuple to the CmdResult
     * @param name : The name of the command
     * @param start : The start time of the command execution
     * @param end : The end time of the command execution
     * @param result : The result of the command in tuple form
     * @return
     */
    fun fromResult(
        name: String,
        start: DateTime,
        end: DateTime,
        result: Try<Any>
    ): CmdResult {
        return CmdResult(
                name = name,
                result = result,
                started = start,
                ended = end,
                totalMs = end.durationFrom(start).toMillis()
        )
    }
}
