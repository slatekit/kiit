package slatekit.jobs.workers

import java.util.concurrent.atomic.AtomicReference
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.jobs.Task
import slatekit.results.Err
import slatekit.tracking.Recorder


/**
 * Optional base class for Workers.
 * All work is done inside this worker which has
 * 1. life-cycle methods : [init], [work], [done]
 * 2. state changes      : [pause], [stop], [resume], [move]
 * 3. alerting ability   : [notify]
 * 4. diagnostic features: [info] method to build diagnostics info
 *
 * NOTES:
 * 1. All anonymous functions supplied to a Job are wrapped in a worker
 * 2. Clients should extend worker to use/enrich the life-cycle, state change, alerting, diagnostic methods above
 */
open class Worker<T>(
    val id: Identity,
    val stats: Recorder<Task, WorkResult, Err> = Recorder.of(id),
    val operation: (suspend (Task) -> WorkResult)? = null
) : StatusCheck {

    protected val _status = AtomicReference<Pair<Status, String>>(Pair(Status.InActive, Status.InActive.name))

    /**
     * ============================================================================
     * PROPERTIES
     * 1. status : for starting, pausing, etc
     * 2. note   : optional reason for any state changes
     * 3. info   : key / value pairs for diagnostics
     * ============================================================================
     */
    override fun status(): Status = _status.get().first

    open fun note():String = _status.get().second

    /**
     * Get key/value pairs representing information about this worker.
     * e.g. such as settings
     */
    open fun info(): List<Pair<String, String>> = listOf()


    /**
     * ============================================================================
     * LIFE-CYCLE Methods
     * 1. init
     * 2. work
     * 3. work(task:Task)
     * 4. done
     * 5. fail
     * ============================================================================
     */
    /**
     * Life-cycle hook to allow for initialization
     */
    open suspend fun init() {
    }

    /**
     * Performs the work
     * This assumes that this work manages it's own work load/queue/source
     */
    open suspend fun work(): WorkResult {
        return work(Task.owned)
    }


    /**
     * Performs the work
     * @param task: The task to perform.
     * NOTE: If this worker manages it's own work load/queue/source, then this task is
     * provided by the work() method and assigned Task.owned. Otherwise, the task is
     * supplied by the @see[slatekit.jobs.Job]
     */
    open suspend fun work(task: Task): WorkResult {
        return when (operation) {
            null -> WorkResult.Done
            else -> operation.invoke(task)
        }
    }

    /**
     * Interface for a Job that can be gracefully paused and resuming.
     * This is possible under various scenarios:
     * 1. job processes tasks from a queue, in which case, when paused, it just resumes by getting the next task
     * 2. job processes paged resources, when its on Page 20, and then paused, it can resume at Page 21
     */

    /**
     * Hook for handling pausing of a job
     * @param reason
     * @return
     */
    open suspend fun pause(reason: String?) {
    }

    /**
     * Hook for handling resuming of a job
     * @param reason
     * @return
     */
    open suspend fun resume(reason: String?, task: Task): WorkResult {
        return work(task)
    }

    /**
     * Hook for handling stopping of a job
     */
    open suspend fun stop(reason: String?){

    }

    /**
     * Life-cycle hook to allow for completion
     */
    open suspend fun done() {
    }

    /**
     * Life-cycle hook to allow for failure
     */
    open suspend fun fail(err: Throwable?) {
        notify("Errored: " + err?.message, null)
    }

    /**
     * ============================================================================
     * STATE MANAGEMENT
     * 1. move(status:Status) to transition status
     * 2. get status
     * ============================================================================
     */
    /**
     * Transition current status to the one supplied
     */
    open suspend fun move(state: Status, note:String? = null) {
        move(state, note, true)
    }

    /**
     * Transition current status to the one supplied
     */
    open suspend fun move(state: Status, note:String?, sendNotification:Boolean) {
        _status.set(Pair(state, note ?: state.name))
        if(sendNotification){
            notify(state.name, null)
        }
    }

    /**
     * ============================================================================
     * NOTIFICATION
     * 1. To notify during state changes, life-cycle events etc
     * ============================================================================
     */
    /**
     * Send out notifications
     */
    open suspend fun notify(desc: String?, extra: List<Pair<String, String>>?) {
    }
}
