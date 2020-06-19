package slatekit.policy.policies

import java.util.concurrent.atomic.AtomicLong
import slatekit.common.log.Logger
import slatekit.policy.Policy
import slatekit.results.Outcome

/**
 * Policy to call the operation supplied after every x calls
 * @param I : Input type
 * @param O : Output type
 */
class Every<I, O>(val limit: Long, val interval: (suspend(I, Outcome<O>) -> Unit)? = null, val logger: Logger? = null) : Policy<I, O> {
    private val count = AtomicLong(0L)

    override suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        val res = operation(i)
        val curr = count.incrementAndGet()
        logger?.info("EVERY: curr = $curr")
        if (curr >= limit) {
            interval?.invoke(i, res)
            count.set(0L)
        }
        return res
    }
}


