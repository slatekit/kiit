package slatekit.policy.policies

import slatekit.common.log.Logger
import slatekit.policy.Policy
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import java.util.concurrent.atomic.AtomicLong

/**
 * Policy to call the operation in steps ( e.g. every 2, 3 times )
 * @param I : Input type
 * @param O : Output type
 */
class Step<I, O>(val steps: Long, val logger: Logger? = null) : Policy<I, O> {
    private val count = AtomicLong(0L)

    override suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        val curr = count.incrementAndGet()
        logger?.info("EVERY: curr = $curr")
        val result = when(curr >= steps) {
            true -> {
                count.set(0L)
                val res = operation(i)
                res
            }
            false -> {
                Outcomes.ignored("Skipping periodic execution")
            }
        }
        return result
    }
}
