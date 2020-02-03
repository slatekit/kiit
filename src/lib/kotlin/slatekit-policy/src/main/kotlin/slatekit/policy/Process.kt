package slatekit.policy

import slatekit.results.Outcome

interface Process<I, O> {
    suspend fun process(i: I, next: suspend(I) -> Outcome<O>): Outcome<O>
}
