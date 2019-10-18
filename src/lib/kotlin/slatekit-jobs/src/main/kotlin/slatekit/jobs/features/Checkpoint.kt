package slatekit.jobs.slatekit.jobs.features

import slatekit.jobs.*
import slatekit.jobs.features.Interval


/**
 * Feature to control a worker based on a number of counts of Status X
 * E.g. Limit the run if counts.totalErrored >= limit supplied
 */
class Checkpoint(interval: Long, val checkpointer:suspend(WorkState.Next, Workable<*>)-> Unit ) : Interval(interval, { state, worker, task ->
    if(state is WorkState.Next) {
        checkpointer(state, worker)
    }
})