package kiit.policy.policies

import slatekit.common.DateTime
import kiit.policy.Policy
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import java.util.concurrent.atomic.AtomicReference



/**
 * Policy to call the operation supplied after every x calls
 * @param I : Input type
 * @param O : Output type
 * @param timeInSeconds: Number of seconds between intervals
 */
class Periodic<I, O>(private val timeInSeconds: Long, val logger: slatekit.common.log.Logger? = null) : Policy<I, O> {
    private val expiry = AtomicReference<DateTime>(null)

    override suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        val curr = expiry.get()
        val now = DateTime.now()
        val result = when(curr) {
            null -> {
                proceed(now, i, operation)
            }
            else -> {
                when(curr.isBefore(now)) {
                    true -> proceed(now, i, operation)
                    false -> Outcomes.ignored("Skipping periodic execution")
                }
            }
        }
        return result
    }


    private suspend fun proceed(timestamp:DateTime, i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        logger?.info("PERIODIC: time = $timestamp")
        val res = operation(i)
        val next = DateTime.now().plusSeconds(timeInSeconds)
        expiry.set(next)
        return res
    }
}
