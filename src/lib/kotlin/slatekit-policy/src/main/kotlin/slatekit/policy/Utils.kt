package slatekit.policy

import slatekit.common.Identity
import slatekit.policy.policies.*
import slatekit.results.Outcome
import slatekit.results.Status
import slatekit.results.Try
import slatekit.results.builders.Outcomes
import slatekit.telemetry.Counters

/**
 * Retryable operation
 */
suspend fun <O> retry(retries: Int, delayMillis: Long, operation: suspend () -> O): Try<O> {
    val policy = Retry<Unit, O>(retries, delayMillis)
    return policy.run(Unit) { Outcomes.success(operation()) }.toTry()
}


/**
 * Retryable operation
 */
fun <O> limit(count: Long, counters: Counters = Counters(Identity.empty), autoProcess:Boolean = true, operation: suspend () -> O): suspend () -> Outcome<O> {
    val policy = Limit<Unit, O>(count, autoProcess, { counters }, null)
    return {
        val result = policy.run(Unit) { Outcomes.success(operation()) }
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


/**
 * Ratio operation which allows at most @see[limit] percent of operations that match the @see[status]
 */
fun <O> ratio(limit: Double, status:Status, counters: Counters = Counters(Identity.empty), call: suspend () -> Outcome<O>): suspend () -> Outcome<O> {
    val policy = Ratio<Unit, O>(limit, status, { counters } , null)
    return {
        policy.run(Unit) { call() }
    }
}


/**
 * Rewrites the raw request
 */
fun <I, O> rewrite(rewriter:suspend (I) -> I, call: suspend (I) -> Outcome<O>): suspend (I) -> Outcome<O> {
    val policy = Rewrite<I, O>(rewriter)
    return { input ->
        policy.run(input){ i -> call(i) }
    }
}
