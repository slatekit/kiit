package slatekit.workers.slatekit.workers

import slatekit.common.DateTime
import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.FunctionMode
import slatekit.common.functions.FunctionResult
import slatekit.results.Result
import slatekit.results.builders.Tries


/**
 * The result of command ( cmd ) that was run
 * @param id      : The id of the job ( a UUID )
 * @param queue   : The name of the queue from which this job came from
 * @param task    : The category of the job ( to distinguish which worker can handle it )
 * @param refId   : Serves as a correlation id
 * @param info     : The command info
 * @param mode     : The mode in which the sync was run ( forced, scheduled )
 * @param result   : Result of execution of the command
 * @param started  : Start time of the command
 * @param ended    : End time of the command
 * @sample:
 *
 *  id       = "ABC123",
 *  queue    = "notifications",
 *  task     = "users.sendWelcomeEmail",
 *  refId    = "abc123"
 */
data class JobResult(val id: String,
                     val queue: String,
                     val task: String,
                     val refId: String,
                     override val info: FunctionInfo,
                     override val mode: FunctionMode,
                     override val result: Result<*, *>,
                     override val started: DateTime,
                     override val ended: DateTime,
                     override val totalMs: Long
) : FunctionResult {


    companion object {
        fun empty(info: FunctionInfo): JobResult {
            val result = Tries.errored<Any>("Not started")
            val start = DateTime.now()
            return JobResult("", "", "", "", info, FunctionMode.Called, result, start, start, 0L)
        }
    }
}