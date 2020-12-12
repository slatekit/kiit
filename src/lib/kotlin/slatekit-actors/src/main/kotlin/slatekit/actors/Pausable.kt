package slatekit.actors

import kotlinx.coroutines.channels.Channel
import slatekit.actors.pause.*

/**
 * Base class to support Actors that can be started, stopped, paused, and resumed
 */
abstract class Pausable<T>(ctx:Context, channel: Channel<Message<T>>)
    : Messageable<T>(ctx, channel), Controls {

    protected val state: State = State { action, oldState, newState
        -> this.changed(action, oldState, newState)
    }

    /**
     * Get running status of this Actor
     */
    fun status(): Status = state.status()


    /**
     * Sends a control message to start, pause, resume, stop processing
     */
    override suspend fun control(action: Action, msg: String?, reference: String): Feedback {
        channel.send(Control<T>(nextId(), action, msg, reference = reference))
        return Feedback(true, "")
    }


    /**
     * Allows the operation to proceed only if this is started or running
     */
    protected suspend fun allow(op:suspend () -> Unit) {
        when(status()) {
            is Status.Started -> op()
            is Status.Running -> op()
            else -> { }
        }
    }


    /**
     * Serves as a hook for implementations to override to listen to state changes
     */
    protected open suspend fun changed(msg: Action, oldStatus: Status, newStatus: Status) {
    }
}

