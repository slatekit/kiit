package slatekit.functions

import kotlinx.coroutines.delay
import slatekit.common.Identity
import slatekit.functions.policy.Every
import slatekit.functions.policy.Limit
import slatekit.results.Outcome
import slatekit.results.Try
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries
import slatekit.tracking.Counters

/**
 * Retryable operation
 */
suspend fun <O> retry(retries: Int, delayMillis: Long, operation: suspend () -> O): Try<O> {
    val r = Tries.attempt { operation() }
    return when {
        r.success -> r
        retries > 0 -> {
            if (delayMillis > 0) {
                delay(delayMillis)
                retry(retries - 1, delayMillis, operation)
            } else {
                retry(retries - 1, delayMillis, operation)
            }
        }
        else -> r
    }
}


/**
 * Retryable operation
 */
fun <O> limit(count: Long, counters: Counters = Counters(Identity.empty), autoProcess:Boolean = true, operation: suspend () -> O): suspend () -> Outcome<O> {
    val limiter = Limit<Unit, O>(count, autoProcess, { counters }, null)
    return {
        val result = limiter.run(Unit) { Outcomes.success(operation()) }
        result
    }
}


/**
 * Retryable operation
 */
fun <O> every(limit: Long, interval: suspend (Outcome<O>) -> Unit, call: suspend () -> Outcome<O>): suspend () -> Outcome<O> {
    val policy = Every<Unit, O>(limit, { _: Unit, result: Outcome<O> -> interval(result) })
    return {
        policy.run(Unit) { call() }
    }
}
