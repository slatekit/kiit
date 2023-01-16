package kiit.policy.policies

import kiit.policy.Policy
import slatekit.results.Outcome

/**
 * Policy to simply execute the operation
 * NOTE: This is the "bottom" most policy and is used to simply wrap the actual call inside a
 * policy so that policies can be "chained" together.
 * @param I : Input type
 * @param O : Output type
 */
class Exec<I, O>(val op: suspend(I) -> Unit) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        op(i)
        return operation(i)
    }
}
