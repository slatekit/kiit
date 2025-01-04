package kiit.actors.pause

import kiit.actors.Action
import kiit.actors.Status
import java.util.concurrent.atomic.AtomicReference

/**
 * Helper class that handles changing the status and sending notifications via the changed handler.
 * 1. start   -> started
 * 2. process -> running
 * 2. stop    -> stopped
 * 3. pause   -> paused
 * 4. resume  -> running
 */
open class State(val changed: (suspend (Action, Status, Status) -> Unit)? = null) {

    private val _status = AtomicReference<Status>(Status.InActive)

    /**
     * Get running status of this Actor
     */
    fun status(): Status = _status.get()

    /**
     * Handles a @see[Control] message to start, stop, pause, resume this actor.
     */
    suspend fun handle(action: Action): Status {
        val oldStatus = _status.get()
        val newStatus = action.toStatus(oldStatus)
        when (action) {
            is Action.Delay -> move(newStatus)
            is Action.Start -> move(newStatus)
            is Action.Resume -> move(newStatus)
            is Action.Stop -> move(newStatus)
            is Action.Kill -> move(newStatus)
            is Action.Pause -> move(newStatus)
            else -> {
            }
        }
        changed?.invoke(action, oldStatus, newStatus)
        return newStatus
    }

    /**
     * Sets the status to running if started
     */
    suspend fun begin(notify: Boolean) {
        val current = status()
        if (current == Status.Started) {
            move(Status.Running)
            if (notify) {
                changed?.invoke(Action.Process, Status.Started, Status.Running)
            }
        }
    }

    /**
     * Moves this actors status to the one supplied.
     */
    suspend fun complete(notify: Boolean) {
        val current = status()
        if (current == Status.Running) {
            move(Status.Completed)
            if (notify) {
                changed?.invoke(Action.Process, Status.Started, Status.Running)
            }
        }
    }

    /**
     * Moves this actors status to the one supplied.
     */
    protected fun move(newStatus: Status) {
        _status.set(newStatus)
    }

    /**
     * Validate the action based on the current status
     */
    fun validate(action: Action): Boolean {
        return when (this.status()) {
            Status.Killed -> action == Action.Check
            else -> true
        }
    }
}
