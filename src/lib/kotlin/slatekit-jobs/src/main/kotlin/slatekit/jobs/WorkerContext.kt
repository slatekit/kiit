package slatekit.jobs.slatekit.jobs

import slatekit.actors.WResult
import slatekit.common.Identity
import slatekit.jobs.Task
import slatekit.tracking.Recorder
import slatekit.policy.Policy
import slatekit.results.Err


/**
 * Represents a request for work by a worker
 * @param context: The current context of the worker including its job, worker, stats, etc
 * @param task : The current task for the worker to work on.
 */
data class WorkRequest(val context: WorkerContext, val task: Task)


/**
 * Represents the context of a Worker containing its statistics, policies, and other components
 * @param id : Identity of its parent ( Job Identity )
 * @param worker : The Worker component itself
 * @param stats : The metrics recorder containing Calls, Counts, Lasts, Logger, etc
 * @param policies : List of policies ( middleware ) associated with this Worker
 * @param task : Empty task, this is used for Self-Managed jobs where there is no Task/Queue.
 *                   This allows passing in a Task which has the job name properly set.
 *                   e.g. signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
 */
data class WorkerContext(
    val id: Identity,
    val worker: Worker<*>,
    val stats: Recorder<Task, WResult, Err>,
    val policies: List<Policy<WorkRequest, WResult>> = listOf(),
    val task: Task = Task.empty.copy(job = id.id)
)
