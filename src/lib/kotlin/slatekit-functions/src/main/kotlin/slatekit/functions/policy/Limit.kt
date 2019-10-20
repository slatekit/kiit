package slatekit.functions.policy

import slatekit.common.metrics.Counters
import slatekit.results.Codes
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Policy to limit the operation based on the total processed items from the metric counts
 * @param I : Input type
 * @param O : Output type
 */
class Limit<I, O>(val limit: Long, val stats: (I) -> Counters) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        val counts = stats(i)
        val pastLimit = counts.totalProcessed() >= limit
        return if (pastLimit) {
            Outcomes.errored(Codes.LIMITED)
        } else {
            operation(i)
        }
    }
}