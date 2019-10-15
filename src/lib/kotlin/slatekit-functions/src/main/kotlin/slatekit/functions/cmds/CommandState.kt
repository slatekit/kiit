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

import slatekit.common.Status
import slatekit.functions.common.*

/**
 *
 * @param name : Name of the command
 * @param lastRun : Last time the command was run
 * @param hasRun : Whether command has run at least once
 * @param lastResult : The last result
 */
data class CommandState(
        override val info: FunctionInfo,
        override val status: Status,
        override val lastResult: CommandResult?
) : FunctionState<CommandResult> {

    /**
     * Builds a copy of the this state with bumped up numbers ( run count, error count, etc )
     * based on the last execution result
     * @param result
     * @return
     */
    fun update(result: CommandResult): CommandState {

        val updated = this.copy(lastResult = result)
        return updated
    }


    companion object {
        /**
         * builds a default Command State
         * @param name
         * @return
         */
        fun empty(info: FunctionInfo): CommandState =
                CommandState(
                        info = info,
                        status = Status.InActive,
                        lastResult = null
                )
    }
}
