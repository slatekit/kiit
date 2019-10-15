package slatekit.jobs

import slatekit.common.ids.Identity
import slatekit.common.metrics.Calls
import slatekit.common.metrics.Lasts
import slatekit.results.Err

/**
 * Used for diagnostics / metrics to track counts of various states and last results
 * of some function/target identified by @param id
 * This serves to track the following:
 *
 * 1. total calls made
 * 2. total calls passed
 * 3. total calls failed
 * 4. last error
 * 5. last time of call
 */
class Tracker<TRequest, TResponse>(
        val id: Identity,
        val calls: Calls,
        val lasts: Lasts<TRequest, TResponse, Err>) {

    companion object {

        fun <TRequest, TResponse> of(id: Identity): Tracker<TRequest, TResponse> {
            return Tracker(id, Calls(id), Lasts(id))
        }
    }
}


