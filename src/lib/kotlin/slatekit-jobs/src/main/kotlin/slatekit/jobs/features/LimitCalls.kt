package slatekit.jobs.features

import slatekit.jobs.*
import slatekit.results.Outcome

/**
 * Feature to control the maximum number of times a worker can be called
 */
class LimitCalls(val limit:Long) : Feature {

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        val calls = worker.stats.calls
        return when(calls) {
            null -> false
            else -> calls.totalRuns() >= limit
        }
    }
}