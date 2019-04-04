package slatekit.core.syncs

import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.core.common.FunctionInfo
import slatekit.core.common.FunctionState

/**
 *
 * @param name : Name of the command
 * @param lastRuntime : Last time the command was run
 * @param hasRun : Whether command has run at least once
 * @param runCount : The total times the command was run
 * @param errorCount : The total errors
 * @param lastResult : The last result
 */
data class SyncState(
        override val info: FunctionInfo,
        override val msg: String,
        override val lastRuntime: DateTime,
        override val hasRun: Boolean,
        override val runCount: Long,
        override val errorCount: Long,
        override val lastResult: SyncResult?
) : FunctionState<SyncResult> {

    /**
     * Builds a copy of the this state with bumped up numbers ( run count, error count, etc )
     * based on the last execution result
     * @param result
     * @return
     */
    fun update(result: SyncResult): SyncState =

            this.copy(
                    msg = result.message ?: "",
                    lastRuntime = result.started,
                    hasRun = true,
                    runCount = runCount + 1,
                    errorCount = (result.error()?.let { errorCount + 1 } ?: errorCount),
                    lastResult = result
            )


    companion object {
        /**
         * builds a default Command State
         * @param name
         * @return
         */
        fun empty(info: FunctionInfo): SyncState =
                SyncState(
                        info = info,
                        msg = "Not yet run",
                        lastRuntime = DateTimes.MIN,
                        hasRun = false,
                        runCount = 0,
                        errorCount = 0,
                        lastResult = null
                )
    }
}