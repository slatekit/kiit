package kiit.policy.policies

import kiit.common.log.Logger
import kiit.policy.Policy
import kiit.telemetry.Counters
import kiit.results.Codes
import kiit.results.Outcome
import kiit.results.builders.Outcomes

/**
 * Policy to limit the operation based on the total processed items from the metric counts
 * @param I : Input type
 * @param O : Output type
 */
class Limit<I, O>(val limit: Long, val autoProcess:Boolean, val stats: (I) -> Counters, val logger: Logger? = null) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        val counts = stats(i)
        val processed = counts.processed.get()
        val pastLimit = processed >= limit
        logger?.info("LIMIT: processed = $processed")
        return if (pastLimit) {
            Outcomes.errored(Codes.LIMITED)
        } else {
            val res = operation(i)
            if(autoProcess){
                counts.processed.inc()
            }
            res
        }
    }
}
