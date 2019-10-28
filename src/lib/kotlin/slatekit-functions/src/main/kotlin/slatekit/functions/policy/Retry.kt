package slatekit.functions.policy

import slatekit.functions.retry
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success

/**
 * Policy to limit the operation based on the total processed items from the metric counts
 * @param I : Input type
 * @param O : Output type
 */
class Retry<I, O>(val retries: Int, val delayMillis: Long) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        return retry(retries, delayMillis) {
            val r = operation(i)
            when (r) {
                is Success -> r.value
                is Failure -> throw r.error.err ?: Exception(r.msg)
            }
        }.toOutcome()
    }
}
