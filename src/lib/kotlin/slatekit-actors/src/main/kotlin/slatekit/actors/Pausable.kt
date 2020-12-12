package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import slatekit.actors.pause.*
import java.util.concurrent.atomic.AtomicLong

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Pausable<T>(val ctx:Context, val channel: Channel<Message<T>>) : Workable, Controls {

    protected val _state: State = State { action, oldState, newState -> this.changed(action, oldState, newState) }
    private val idGen = AtomicLong(0L)


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
     * Launches this actor to start processing messages.
     * This launches on the scope supplied in the context
     */
    override suspend fun work(): Job {
        return ctx.scope.launch {
            for (msg in channel) {
                track(Puller.WORK, msg)
                work(msg)
                yield()
            }
        }
    }


    /**
     *  Handles each message based on its type @see[Content], @see[Control],
     *  This handles following message types and moves this actor to a running state correctly
     *  1. @see[Control] messages to start, stop, pause, resume the actor
     *  2. @see[Request] messages to load payloads from a source ( e.g. queue )
     *  3. @see[Content] messages are simply delegated to the work method
     */
    protected open suspend fun work(item: Message<T>) {
        when (item) {
            is Control -> {
                _state.handle(item.action)
            }
            is Content -> {
                _state.begin();
                handle(Action.Process, item.reference, item.data)
            }
            else -> {
                // Does not support Request<T>
            }
        }
    }

    /**
     * This is the only method to implement to handle the actual work.
     */
    protected abstract suspend fun handle(action: Action, target: String, item: T)


    /**
     * Serves as a hook for implementations to override to listen to state changes
     */
    protected open suspend fun changed(msg: Action, oldStatus: Status, newStatus: Status) {
    }


    /**
     * Serves as a hook for implementations to override for add custom diagnostics
     */
    protected open suspend fun track(source: String, data: Message<T>) {
    }


    /**
     * Gets the next id used in creating a new Message
     */
    protected fun nextId(): Long = idGen.incrementAndGet()
}

