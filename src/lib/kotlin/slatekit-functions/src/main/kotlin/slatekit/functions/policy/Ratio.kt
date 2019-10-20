package slatekit.functions.policy

import slatekit.common.metrics.Counters
import slatekit.results.Codes
import slatekit.results.Outcome
import slatekit.results.Status
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
class Ratio<I, O>(val limit: Double, val status: Status, val stats: (I) -> Counters) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        // NOTE: It is up to the caller to keep track of the counts
        val result = operation(i)
        val counts = stats(i)
        val isMatch = when (status) {
            is Status.Succeeded  -> counts.totalSucceeded()  / counts.totalProcessed() >= limit
            is Status.Denied     -> counts.totalDenied()     / counts.totalProcessed() >= limit
            is Status.Invalid    -> counts.totalInvalid()    / counts.totalProcessed() >= limit
            is Status.Ignored    -> counts.totalIgnored()    / counts.totalProcessed() >= limit
            is Status.Errored    -> counts.totalErrored()    / counts.totalProcessed() >= limit
            is Status.Unexpected -> counts.totalUnexpected() / counts.totalProcessed() >= limit
            else -> counts.totalUnexpected() / counts.totalProcessed() >= limit
        }
        return if(isMatch) {
            Outcomes.errored(Codes.LIMITED)
        } else {
            result
        }
    }
}