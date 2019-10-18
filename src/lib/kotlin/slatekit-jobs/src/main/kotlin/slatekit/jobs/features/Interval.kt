package slatekit.jobs.features

import slatekit.jobs.*
import slatekit.results.Outcome
import java.util.concurrent.atomic.AtomicLong


/**
 * Feature to control a worker based on a number of counts of Status X
 * E.g. Limit the run if counts.totalErrored >= limit supplied
 */
open class Interval(val interval: Long, val operation:suspend(WorkState, Workable<*>, Task) -> Unit) : Feature {
    private val count = AtomicLong(0L)

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        val curr = count.incrementAndGet()
        if(curr >= interval) {
            state.onSuccess {
                operation(it, worker, task)
            }
        }
        return true
    }
}