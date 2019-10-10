package slatekit.common.metrics

import slatekit.common.ids.Identity
import slatekit.results.Err

class Tracker<TRequest, TResult>(
        val id:Identity,
        val counts: Counters,
        val lasts: Lasts<TRequest, TResult, Err>) {

    companion object {

        fun <TRequest, TResult> of(id: Identity): Tracker<TRequest, TResult> {
            return Tracker(id, Counters(id), Lasts(id))
        }
    }
}