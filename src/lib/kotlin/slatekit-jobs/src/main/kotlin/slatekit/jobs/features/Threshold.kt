package slatekit.jobs.features

import slatekit.jobs.*
import slatekit.results.Outcome
import slatekit.results.Status

/**
 * Feature to control a worker based on a percentage / threshold of counts of Status X compared to totalRequests.
 * E.g. Limit the run if counts.totalErrored is 10% of counts.totalProcessed
 */
class Threshold(val limit: Double, val status: Status) : Feature {

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        val counts = worker.stats.counts
        return when (counts) {
            null -> false
            else -> {
                when (status) {
                    is Status.Succeeded -> counts.totalSucceeded() / counts.totalProcessed() >= limit
                    is Status.Denied -> counts.totalDenied() / counts.totalProcessed() >= limit
                    is Status.Invalid -> counts.totalInvalid() / counts.totalProcessed() >= limit
                    is Status.Ignored -> counts.totalIgnored() / counts.totalProcessed() >= limit
                    is Status.Errored -> counts.totalErrored() / counts.totalProcessed() >= limit
                    is Status.Unexpected -> counts.totalUnexpected() / counts.totalProcessed() >= limit
                    else -> counts.totalUnexpected() / counts.totalProcessed() >= limit
                }
            }
        }
    }
}