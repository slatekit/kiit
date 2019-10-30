package slatekit.functions

import slatekit.results.Outcome

interface Output<I, O> {
    suspend fun process(raw:I, i: Outcome<I>, o: Outcome<O>): Outcome<O>
}
