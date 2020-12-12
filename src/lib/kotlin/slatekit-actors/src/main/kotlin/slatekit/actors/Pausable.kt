package slatekit.actors

import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicLong

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Pausable<T>(val channel: Channel<Message<T>>) : Controls {

    protected val _state:State = State { action, oldState, newState -> this.changed(action, oldState, newState) }
    protected val idGen = AtomicLong(0L)


    /**
     * Get running status of this Actor
     */
    fun status(): Status = _state.status()


    /**
     * Sends a control message to start, pause, resume, stop processing
     */
    override suspend fun control(action: Action, msg: String?, reference: String): Feedback {
        channel.send(Control<T>(nextId(), action, msg, reference = reference))
        return Feedback(true, "")
    }


    /**
     * Serves as a hook for implementations to override to listen to state changes
     */
    protected open suspend fun changed(msg: Action, oldStatus: Status, newStatus: Status) {
    }


    /**
     * Gets the next id used in creating a new Message
     */
    protected fun nextId(): Long = idGen.incrementAndGet()
}

