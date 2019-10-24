package slatekit.functions

import slatekit.results.Outcome


interface Input<I> {
    suspend fun process(i: Outcome<I>):Outcome<I>
}


