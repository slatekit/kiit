package slatekit.functions.policy

import slatekit.results.Outcome

/**
 * Policy to simply execute the operation
 * NOTE: This is the "bottom" most policy and is used to simply wrap the actual call inside a
 * policy so that policies can be "chained" together.
 * @param I : Input type
 * @param O : Output type
 */
class Exec<I, O> : Policy<I, O> {

    override suspend fun run(i: I, operation: suspend (I) -> Outcome<O>): Outcome<O> {
        return operation(i)
    }
}