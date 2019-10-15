package slatekit.functions.syncs

import slatekit.common.DateTime
import slatekit.functions.common.*
import slatekit.results.Result
import slatekit.results.builders.Tries


/**
 * The result of command ( cmd ) that was run
 * @param info     : The command info
 * @param mode     : The mode in which the sync was run ( forced, scheduled )
 * @param count    : The number of items synced
 * @param result   : Result of execution of the command
 * @param started  : Start time of the command
 * @param ended    : End time of the command
 */
data class SyncResult(
        val count: Int,
        override val info: FunctionInfo,
        override val mode: FunctionMode,
        override val result: Result<*, *>,
        override val started: DateTime,
        override val ended: DateTime
) : FunctionResult {

    companion object {
        fun empty(info: FunctionInfo): SyncResult {
            val result = Tries.errored<Any>("Not started")
            val start = DateTime.now()
            return SyncResult(0, info, FunctionMode.Called, result, start, start)
        }
    }
}