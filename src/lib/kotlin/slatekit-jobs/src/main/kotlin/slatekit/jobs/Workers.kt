package slatekit.jobs

import slatekit.common.Identity
import slatekit.actors.Status
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


/**
 * Represents a cluster of Workers that are affiliated with 1 job.
 * This helps manage the coordination between a @see[Worker] and a @see[slatekit.jobs.Job]
 * This is done by this class interpreting the @see[WorkResult] returned by a Worker
 * Based on the WorkResult, this may send commands @see[slatekit.jobs.support.Command]s to a Job's Channel
 * Essentially, this works like a glorified loop over each work, continuously:
 * 1. checking its WorkResult
 * 2. sending more commands to the Job's channel to pause, resume, process etc.
 * 3. handling errors
 * 4. notification of state changes
 */
class Workers(val ctx: Context) {
    val events = ctx.notifier.wrkEvents
    val contexts = ctx.workers.map { WorkerContext(it.id, it, Recorder.of(it.id), ctx.policies) }
    private val lookup: Map<String, WorkerContext> = contexts.map { it.id.id to it }.toMap()

    /**
     * Subscribe to status being changed for any worker
     */
    suspend fun on(op: suspend (Event) -> Unit) {
        events.on(op)
    }

    /**
     * Subscribe to status beging changed to the one supplied for any worker
     */
    suspend fun on(status: Status, op: suspend (Event) -> Unit) {
        events.on(status.name, op)
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(id: Identity): WorkerContext? = when (lookup.containsKey(id.id)) {
        true -> lookup[id.id]
        false -> null
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(id: String): WorkerContext? = when (lookup.containsKey(id)) {
        true -> lookup[id]
        false -> null
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(index:Int): WorkerContext? = contexts[index]

    /**
     * Gets all the worker ids
     */
    fun getIds(): List<Identity> = ctx.workers.map { it.id }
}
