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


/**
 *
 * @param name        : Name of the command
 * @param lastRuntime : Last time the command was run
 * @param hasRun      : Whether command has run at least once
 * @param runCount    : The total times the command was run
 * @param errorCount  : The total errors
 * @param lastResult  : The last result
 */
data class CmdState(
        val name: String,
        val msg: String,
        val lastRuntime: DateTime,
        val hasRun: Boolean,
        val runCount: Int,
        val errorCount: Int,
        val lastResult: CmdResult?
) {
    /**
     * Builds a copy of the this state with bumped up numbers ( run count, error count, etc )
     * based on the last execution result
     * @param result
     * @return
     */
    fun update(result: CmdResult): CmdState =

            this.copy(
                    msg = result.message ?: "",
                    lastRuntime = result.started,
                    hasRun = true,
                    runCount = runCount + 1,
                    errorCount = (result.error?.let { errorCount + 1 } ?: errorCount),
                    lastResult = result
            )
}
