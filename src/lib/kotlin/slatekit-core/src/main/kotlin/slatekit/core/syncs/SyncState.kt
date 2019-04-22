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

package slatekit.core.syncs

import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Status
import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.FunctionMode
import slatekit.common.functions.FunctionState
import slatekit.common.metrics.Metrics
import slatekit.common.metrics.MetricsLite

/**
 *
 * @param name : Name of the command
 * @param lastRun : Last time the command was run
 * @param hasRun : Whether command has run at least once
 * @param lastResult : The last result
 */
data class SyncState(
        override val info: FunctionInfo,
        override val status: Status,
        override val msg: String,
        override val lastRun: DateTime,
        override val lastMode: FunctionMode,
        override val hasRun: Boolean,
        override val metrics: Metrics,
        override val lastResult: SyncResult?
) : FunctionState<SyncResult> {

    /**
     * Builds a copy of the this state with bumped up numbers ( run count, error count, etc )
     * based on the last execution result
     * @param result
     * @return
     */
    fun update(result: SyncResult): SyncState {

        val updated = this.copy(
                msg = result.message,
                lastMode = result.mode,
                lastRun = result.started,
                hasRun = true,
                lastResult = result
        )

        // Update the metrics based on the slatekit.results.status.code
        // which is standardized across all modules
        updated.increment(result.result.code, null)
        return updated
    }


    companion object {
        /**
         * builds a default Command State
         * @param name
         * @return
         */
        fun empty(info: FunctionInfo): SyncState =
                SyncState(
                        info = info,
                        status = Status.InActive,
                        msg = "Not yet run",
                        lastMode = FunctionMode.Called,
                        lastRun = DateTimes.MIN,
                        hasRun = false,
                        metrics = MetricsLite(),
                        lastResult = null
                )
    }
}