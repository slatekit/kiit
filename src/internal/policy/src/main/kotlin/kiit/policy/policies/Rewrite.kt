package kiit.policy.policies

import kiit.policy.Policy
import slatekit.results.Outcome

/**
 * Policy to rewrite / transform the input before applying the operation
 * @param I : Input type
 * @param O : Output type
 */
class Rewrite<I, O>(val rewrite: suspend (I) -> (I)) : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        val r = rewrite(i)
        return operation(r)
    }
}
