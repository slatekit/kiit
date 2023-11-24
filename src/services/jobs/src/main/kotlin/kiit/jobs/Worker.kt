package kiit.jobs

import java.util.concurrent.atomic.AtomicReference
import kiit.common.Identity
import kiit.actors.Status
import kiit.actors.pause.Check


/**
 * Optional base class for Workers.
 * All work is done inside this worker which has
 * 1. life-cycle methods : [started], [work], [completed]
 * 2. state changes      : [paused], [stopped], [resumed], [move]
 * 3. alerting ability   : [notify]
 * 4. diagnostic features: [info] method to build diagnostics info
 *
 * NOTES:
 * 1. All anonymous functions supplied to a Job are wrapped in a worker
 * 2. Clients should only extend worker to use/enrich the life-cycle, state change, alerting, diagnostic methods above
 */
open class Worker<T>(id: Identity) : Check, Cycle {
    val id = if (id.tags.isEmpty() || !id.tags.contains("worker")) id.with(null, tags = listOf("worker")) else id
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


    open fun note(): String = _status.get().second


    /**
     * Get key/value pairs representing information about this worker.
     * e.g. such as settings
     */
    open fun info(): List<Pair<String, String>> = listOf()


    /**
     * Performs the work
     * @param task: The task to perform.
     * NOTE: If this worker manages it's own work load/queue/source, then this task is
     * provided by the work() method and assigned Task.owned. Otherwise, the task is
     * supplied by the @see[kiit.jobs.Manager]
     */
    open suspend fun work(task: Task): WResult {
        return WResult.Done
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
    open suspend fun move(state: Status, note: String? = null) {
        move(state, note, true)
    }


    /**
     * Transition current status to the one supplied
     */
    open suspend fun move(state: Status, note: String?, sendNotification: Boolean) {
        _status.set(Pair(state, note ?: state.name))
        if (sendNotification) {
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
