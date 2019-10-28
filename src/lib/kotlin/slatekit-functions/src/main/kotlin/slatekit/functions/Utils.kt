package slatekit.functions

import kotlinx.coroutines.delay
import slatekit.results.Try
import slatekit.results.builders.Tries


/**
 * Retryable operation
 */
suspend fun <O> retry(retries:Int, operation: suspend () -> O) :Try<O> = retry(retries, 0L, operation)


/**
 * Retryable operation
 */
suspend fun <O> retry(retries:Int, delayMillis:Long, operation: suspend () -> O) :Try<O> {
    val r = Tries.attempt { operation() }
    return when {
        r.success     -> r
        retries > 0 -> {
            if(delayMillis > 0 ) {
                delay(delayMillis)
                retry(retries - 1, delayMillis, operation)
            } else {
                retry(retries - 1, delayMillis, operation)
            }
        }
        else          -> r
    }
}



