package kiit.actors

import kotlinx.coroutines.channels.Channel
import kiit.actors.pause.Controls
import kiit.actors.pause.Check
import kiit.actors.pause.Feedback
import kiit.actors.pause.State

/**
 * Base class to support Actors that can be started, stopped, paused, and resumed
 */
abstract class Pausable<T>(ctx: Context, channel: Channel<Message<T>>, private val enableStrictMode: Boolean) :
    Messageable<T>(ctx, channel), Controls, Check {

    protected val state: State = State { action, oldState, newState
        ->
        this.onChanged(action, oldState, newState)
    }

    /**
     * Get running status of this Actor
     */
    override fun status(): Status = state.status()

    /**
     * Sends a control message to start, pause, resume, stop processing
     */
    override suspend fun control(action: Action, msg: String?, reference: String): Feedback {
        channel.send(Control<T>(nextId(), action, msg, reference = reference))
        return Feedback(true, "")
    }

    /**
     * Forces an immediate change to running status
     */
    override suspend fun force(action: Action, msg: String?, reference: String): Feedback {
        val oldStatus = status()
        val newStatus = state.handle(action)
        return when (oldStatus != newStatus) {
            true -> {
                onChanged(action, oldStatus, newStatus)
                Feedback(true, "")
            }

            false -> Feedback(false, "Unable to force change, current status = : ${status().name}")
        }
    }

    /**
     * Allows the operation to proceed only if this is started or running
     */
    protected suspend fun allow(op: suspend () -> Unit): Receipt {
        return when (enableStrictMode) {
            false -> {
                op()
                Receipt.Accepted
            }

            true -> when (status()) {
                is Status.Started -> {
                    op(); Receipt.Accepted
                }

                is Status.Running -> {
                    op(); Receipt.Accepted
                }

                else -> {
                    Receipt.Rejected
                }
            }
        }
    }

    /**
     * Serves as a hook for implementations to override to listen to state changes
     */
    protected open suspend fun onChanged(action: Action, oldStatus: Status, newStatus: Status) {
    }
}
