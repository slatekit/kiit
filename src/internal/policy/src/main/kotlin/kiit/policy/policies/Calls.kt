package kiit.policy.policies

import kiit.policy.Policy
import kiit.telemetry.Calls
import kiit.results.Codes
import kiit.results.Outcome
import kiit.results.builders.Outcomes

/**
 * Policy to limit the operation based on the total calls/runs
 * @param I : Input type
 * @param O : Output type
 */
class Calls<I, O>(val limit: Long, val stats: (I) -> Calls) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        val calls = stats(i)
        val pastLimit = calls.totalRuns() >= limit
        return if (pastLimit) {
            Outcomes.errored(Codes.LIMITED)
        } else {
            operation(i)
        }
    }
}
