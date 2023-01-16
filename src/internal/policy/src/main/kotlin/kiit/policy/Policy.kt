package kiit.policy

import slatekit.results.Outcome

/**
 * @param I : Input type
 * @param O : Output type
 */
interface Policy<I, O> {
    suspend fun run(i: I, operation: suspend(I) -> Outcome<O>): Outcome<O>
}
