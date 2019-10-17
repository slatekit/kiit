package slatekit.jobs.features

import slatekit.jobs.JobContext
import slatekit.jobs.Task
import slatekit.jobs.WorkState
import slatekit.jobs.Workable
import slatekit.jobs.Strategy
import slatekit.results.Outcome


/**
 * Base convenience rule to delegate handling based on the success/failure of the run
 */
open class Handler(val onSuccess:Boolean, val operation:suspend(WorkState, Workable<*>, Task) -> WorkState ) : Strategy {

    override suspend fun check(context: JobContext, worker: Workable<*>, task: Task, state: Outcome<WorkState>): Boolean {
        return process(onSuccess, context, worker, task, state, operation)
    }
}