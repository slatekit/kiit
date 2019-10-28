package slatekit.functions

import slatekit.results.Outcome

interface Output<I, O> {
    suspend fun process(i: I, o: Outcome<O>): Outcome<O>
}
