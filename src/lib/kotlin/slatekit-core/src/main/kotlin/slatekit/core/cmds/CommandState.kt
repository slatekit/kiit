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
import slatekit.common.DateTimes

/**
 *
 * @param name : Name of the command
 * @param lastRuntime : Last time the command was run
 * @param hasRun : Whether command has run at least once
 * @param runCount : The total times the command was run
 * @param errorCount : The total errors
 * @param lastResult : The last result
 */
data class CommandState(
    val name: String,
    val msg: String,
    val lastRuntime: DateTime,
    val hasRun: Boolean,
    val runCount: Int,
    val errorCount: Int,
    val lastResult: CommandResult?
) {
    /**
     * Builds a copy of the this state with bumped up numbers ( run count, error count, etc )
     * based on the last execution result
     * @param result
     * @return
     */
    fun update(result: CommandResult): CommandState =

            this.copy(
                    msg = result.message ?: "",
                    lastRuntime = result.started,
                    hasRun = true,
                    runCount = runCount + 1,
                    errorCount = (result.error?.let { errorCount + 1 } ?: errorCount),
                    lastResult = result
            )


    companion object {
        /**
         * builds a default Command State
         * @param name
         * @return
         */
        fun empty(name: String): CommandState =
                CommandState(
                        name = name,
                        msg = "Not yet run",
                        lastRuntime = DateTimes.MIN,
                        hasRun = false,
                        runCount = 0,
                        errorCount = 0,
                        lastResult = null
                )
    }
}
