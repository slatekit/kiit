package kiit.policy.policies

import kiit.policy.Policy
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import java.util.concurrent.atomic.AtomicLong

/**
 * Policy to call operation when both op1 and op2 succeed
 * @param I : Input type
 * @param O : Output type
 * @param op1: The first operation to perform
 * @param op2: The second operation to perform
 * @param empty: An empty success state as a place holder for op1 and op2
 */
class And<I, O>(val op1: Policy<I, O>,
                val op2: Policy<I, O>,
                val empty: Outcome<O>,
                val logger: slatekit.common.log.Logger? = null) : Policy<I, O> {
    private val count = AtomicLong(0L)

    override suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O> {
        val res1 = op1.run(i) { empty }
        val res2 = op2.run(i) { empty }
        val result = when(res1.success && res2.success) {
            true -> operation(i)
            false -> when(res1) {
                is Success -> res2
                is Failure -> res1
            }
        }
        return result
    }
}
