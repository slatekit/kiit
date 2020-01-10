package slatekit.functions.policy

import slatekit.common.log.Logger
import slatekit.results.*
import slatekit.tracking.Counters
import slatekit.results.builders.Outcomes

/**
 * Policy to limit the operation based on the ration/threshold of Outcome.status
 * @sample : this allows for error rates/thresholds.
 * @param limit: The limit in percentage of the status
 * @param status: The status to check for
 * @param status: The callback to get the counters for the status
 * @param I : Input type
 * @param O : Output type
 */
class Ratio<I, O>(val limit: Double, val status: Status, val stats: (I) -> Counters, val logger: Logger? = null) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        // NOTE: It is up to the caller to keep track of the counts
        val result = operation(i)
        val counts = stats(i)
        val isMatch = when (status) {
            is Passed.Succeeded -> isAtThreshold(counts.totalSucceeded(), counts.totalProcessed())
            is Failed.Denied -> isAtThreshold(counts.totalDenied(), counts.totalProcessed())
            is Failed.Invalid -> isAtThreshold(counts.totalInvalid(), counts.totalProcessed())
            is Failed.Ignored -> isAtThreshold(counts.totalIgnored(), counts.totalProcessed())
            is Failed.Errored -> isAtThreshold(counts.totalErrored(), counts.totalProcessed())
            is Failed.Unexpected -> isAtThreshold(counts.totalUnexpected(), counts.totalProcessed())
            else -> isAtThreshold(counts.totalUnexpected(), counts.totalProcessed())
        }
        logger?.info("RATIO: status = ${result.status.msg}")
        return if (isMatch) {
            Outcomes.errored(Codes.LIMITED)
        } else {
            result
        }
    }

    private fun isAtThreshold(count: Long, total: Long): Boolean {
        val rate: Double = if (total <= 0) 0.0 else count / total.toDouble()
        return rate >= limit
    }
}
