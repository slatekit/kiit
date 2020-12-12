package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Managed<T>(override val ctx: Context, val channel: Channel<Message<T>>) : Actor<T>, Controls {

    protected val _status = AtomicReference<Status>(Status.InActive)
    protected val idGen = AtomicLong(0L)


    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    override val id: String
        get() {
            return ctx.id
        }


    /**
     * Get running status of this Actor
     */
    fun status(): Status = _status.get()

    
    /**
     * Sends a content message using the payload supplied
     * @param item : The payload to process
     */
    override suspend fun send(item: T) {
        channel.send(Control<T>(nextId(), Action.Process, reference = Reference.NONE))
    }


    /**
     * Sends a content message using the payload and target supplied
     * @param item  : The payload to process
     * @param reference: Target name which can be anything for implementing class
     *                This is available in the Message class
     */
    override suspend fun send(item: T, reference: String) {
        channel.send(Control<T>(nextId(), Action.Process, reference = reference))
    }


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
    open suspend fun work(item: Message<T>) {
        when (item) {
            is Control -> {
                handle(item)
            }
            is Content -> {
                begin(); handle(Action.Process, item.reference, item.data)
            }
            else -> {
                // Does not support Request<T>
            }
        }
    }


    /**
     * Handles a @see[Control] message to start, stop, pause, resume this actor.
     */
    protected open suspend fun handle(msg: Control<T>) {
        val action = msg.action
        val oldStatus = _status.get()
        val newStatus = action.toStatus(oldStatus)
        if (newStatus == Status.Stopped) {
            println("STOPPING:")
        }
        when (action) {
            is Action.Delay -> move(newStatus)
            is Action.Start -> move(newStatus)
            is Action.Resume -> move(newStatus)
            is Action.Stop -> move(newStatus)
            is Action.Kill -> move(newStatus)
            is Action.Pause -> {
                move(newStatus)
                ctx.scheduler.schedule(msg.seconds ?: 30) { resume() }
            }
            else -> {

            }
        }
        changed(msg, oldStatus, newStatus)
    }


    /**
     * This is the only method to implement to handle the actual work.
     */
    protected abstract suspend fun handle(action: Action, target: String, item: T)


    /**
     * Serves as a hook for implementations to override to listen to state changes
     */
    protected open suspend fun changed(msg: Control<T>, oldStatus: Status, newStatus: Status) {
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


    /**
     * Moves this actors status to the one supplied
     */
    protected fun move(newStatus: Status) {
        _status.set(newStatus)
    }


    /**
     * Validate the action based on the current status
     */
    protected fun validate(action: Action): Boolean {
        return when (this.status()) {
            Status.Killed -> action == Action.Check
            else -> true
        }
    }


    /**
     * Sets the status to running if started
     */
    protected fun begin() {
        val current = status()
        if (current == Status.Started) {
            move(Status.Running)
        }
    }
}
