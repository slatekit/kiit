package slatekit.policy

import slatekit.results.Outcome

interface Output<I, O> {
    suspend fun process(raw:I, i: Outcome<I>, o: Outcome<O>): Outcome<O>
}
