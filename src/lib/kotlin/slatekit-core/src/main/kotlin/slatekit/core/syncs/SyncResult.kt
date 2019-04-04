package slatekit.core.syncs

import slatekit.common.DateTime
import slatekit.core.common.FunctionInfo
import slatekit.core.common.FunctionResult
import slatekit.results.Result
import slatekit.results.builders.Tries


/**
 * The result of command ( cmd ) that was run
 * @param info     : The command info
 * @param result   : Result of execution of the command
 * @param started  : Start time of the command
 * @param ended    : End time of the command
 */
data class SyncResult(
        override val info: FunctionInfo,
        override val result: Result<*, *>,
        override val started: DateTime,
        override val ended: DateTime,
        override val totalMs: Long
) : FunctionResult {


    companion object {
        fun empty(info: FunctionInfo): SyncResult {
            val result = Tries.errored<Any>("Not started")
            val start = DateTime.now()
            return SyncResult(info, result, start, start, 0L)
        }
    }
}