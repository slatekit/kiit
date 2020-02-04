package slatekit.jobs

import slatekit.common.Identity
import slatekit.jobs.slatekit.jobs.support.Backoffs
import slatekit.tracking.Recorder
import slatekit.policy.Policy

/**
 * Represents the context of a Worker
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
    val stats: Recorder<Task, WorkState>,
    val backoffs:Backoffs = Backoffs(),
    val policies: List<Policy<WorkRequest, WorkResult>> = listOf(),
    val task: Task = Task.empty.copy(job = id.id)
)
