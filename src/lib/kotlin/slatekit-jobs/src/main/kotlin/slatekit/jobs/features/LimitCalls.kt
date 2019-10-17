package slatekit.jobs.features

import slatekit.jobs.*
import slatekit.results.Outcome

/**
 * Rule to control the maximum number of times a worker can be called
 */
class LimitCalls(val limit:Long) : Strategy {

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        val calls = worker.stats.calls
        return when(calls) {
            null -> false
            else -> calls.totalRuns() >= limit
        }
    }
}