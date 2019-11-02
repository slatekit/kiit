package slatekit.functions.policy

import slatekit.common.log.Logger
import slatekit.tracking.Counters
import slatekit.results.Codes
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Policy to limit the operation based on the total processed items from the metric counts
 * @param I : Input type
 * @param O : Output type
 */
class Limit<I, O>(val limit: Long, val stats: (I) -> Counters, val logger: Logger? = null) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        val counts = stats(i)
        val processed = counts.totalProcessed()
        val pastLimit = processed >= limit
        logger?.info("LIMIT: processed = $processed")
        return if (pastLimit) {
            Outcomes.errored(Codes.LIMITED)
        } else {
            operation(i)
        }
    }
}
