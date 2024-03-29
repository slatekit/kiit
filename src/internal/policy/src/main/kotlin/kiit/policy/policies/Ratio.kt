package kiit.policy.policies

import kiit.common.log.Logger
import kiit.policy.Policy
import kiit.results.*
import kiit.telemetry.Counters
import kiit.results.builders.Outcomes

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
            is Passed.Succeeded -> isAtThreshold(counts.succeeded.get(), counts.processed.get())
            is Failed.Denied -> isAtThreshold(counts.denied.get(), counts.processed.get())
            is Failed.Invalid -> isAtThreshold(counts.invalid.get(), counts.processed.get())
            is Failed.Ignored -> isAtThreshold(counts.ignored.get(), counts.processed.get())
            is Failed.Errored -> isAtThreshold(counts.errored.get(), counts.processed.get())
            is Failed.Unknown -> isAtThreshold(counts.unknown.get(), counts.processed.get())
            else -> isAtThreshold(counts.unknown.get(), counts.processed.get())
        }
        logger?.info("RATIO: status = ${result.status.desc}")
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
