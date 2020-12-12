package slatekit.actors

import java.util.concurrent.atomic.AtomicReference

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
open class State(val changed:(suspend(Action, Status, Status) -> Unit)? = null)  {

    private val _status = AtomicReference<Status>(Status.InActive)

    /**
     * Get running status of this Actor
     */
    fun status(): Status = _status.get()


    /**
     * Handles a @see[Control] message to start, stop, pause, resume this actor.
     */
    suspend fun handle(action: Action) {
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
    }


    /**
     * Moves this actors status to the one supplied
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


    /**
     * Sets the status to running if started
     */
    fun begin() {
        val current = status()
        if (current == Status.Started) {
            move(Status.Running)
        }
    }
}
