package slatekit.policy.policies

import slatekit.policy.Policy
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import java.util.concurrent.atomic.AtomicLong

/**
 * Policy to call operation when either op1 and op2 succeed
 * @param I : Input type
 * @param O : Output type
 * @param op1: The first operation to perform
 * @param op2: The second operation to perform if op1 fails
 */
class Or<I, O>(val op1: Policy<I, O>,
               val op2: Policy<I, O>,
               val logger: slatekit.common.log.Logger? = null) : Policy<I, O> {
    private val count = AtomicLong(0L)

    override suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        val res1 = op1.run(i, operation)
        val result = when(res1) {
            is Success -> res1
            is Failure -> op2.run(i, operation)
        }
        return result
    }
}
