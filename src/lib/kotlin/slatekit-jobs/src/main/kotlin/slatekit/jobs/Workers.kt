package slatekit.jobs

import slatekit.common.Identity
import slatekit.actors.Status
import slatekit.tracking.Recorder
import slatekit.jobs.slatekit.jobs.WorkContext

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
    val contexts = ctx.workers.map { WorkContext(it.id, it, Recorder.of(it.id), ctx.policies) }
    private val lookup: Map<String, WorkContext> = contexts.map { it.id.id to it }.toMap()

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
    operator fun get(id: Identity): WorkContext? = when (lookup.containsKey(id.id)) {
        true -> lookup[id.id]
        false -> null
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(id: String): WorkContext? = when (lookup.containsKey(id)) {
        true -> lookup[id]
        false -> null
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(index:Int): WorkContext? = contexts[index]

    /**
     * Gets all the worker ids
     */
    fun getIds(): List<Identity> = ctx.workers.map { it.id }
}
