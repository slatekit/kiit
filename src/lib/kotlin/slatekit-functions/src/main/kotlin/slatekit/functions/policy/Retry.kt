package slatekit.functions.policy

import kotlinx.coroutines.delay
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Tries
import slatekit.tracking.Counter

/**
 * Policy to limit the operation based on the total processed items from the metric counts
 * @param I : Input type
 * @param O : Output type
 */
class Retry<I, O>(val retries: Int, val delayMillis: Long) : Policy<I, O> {
    val counts = Counter()

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        val r = Tries.of { operation(i) }
        val attempts = counts.inc()
        return when(r) {
            is Success -> r.value
            is Failure -> {
                if(attempts < retries){
                    if (delayMillis > 0) {
                        delay(delayMillis)
                    }
                    run(i, operation)
                }
                r.toOutcome()
            }
        }
    }
}
