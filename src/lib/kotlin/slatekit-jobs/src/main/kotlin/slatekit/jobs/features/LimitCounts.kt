package slatekit.jobs.features

import slatekit.jobs.*
import slatekit.results.Outcome
import slatekit.results.Status

/**
 * Feature to control a worker based on a number of counts of Status X
 * E.g. Limit the run if counts.totalErrored >= limit supplied
 */
class LimitCounts(val limit: Long, val status: Status?) : Feature {

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        val counts = worker.stats.counts
        return when (counts) {
            null -> false
            else -> {
                when (status) {
                    null -> counts.totalProcessed() >= limit
                    is Status.Succeeded -> counts.totalSucceeded() >= limit
                    is Status.Denied -> counts.totalDenied() >= limit
                    is Status.Invalid -> counts.totalInvalid() >= limit
                    is Status.Ignored -> counts.totalIgnored() >= limit
                    is Status.Errored -> counts.totalErrored() >= limit
                    is Status.Unexpected -> counts.totalUnexpected() >= limit
                    else -> counts.totalUnexpected() >= limit
                }
            }
        }
    }
}