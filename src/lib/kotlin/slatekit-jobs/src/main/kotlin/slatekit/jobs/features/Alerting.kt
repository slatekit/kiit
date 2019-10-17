package slatekit.jobs.features

import slatekit.common.DateTime
import slatekit.common.metrics.Event
import slatekit.jobs.*
import slatekit.jobs.support.JobUtils
import slatekit.results.Outcome
import java.util.concurrent.atomic.AtomicLong


/**
 * Rule to control a worker based on a number of counts of Status X
 * E.g. Limit the run if counts.totalErrored >= limit supplied
 */
open class Alerting(val desc:String, val target:String, val interval: Long, val started:DateTime, val alerter:suspend(Event)-> Unit ) : Strategy {
    private val count = AtomicLong(0L)

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        val curr = count.incrementAndGet()
        if (curr >= interval) {
            val event = JobUtils.toEvent(started, desc, target, context, worker, task, state)
            alerter(event)
        }
        return true
    }
}