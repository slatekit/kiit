package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Managed<T>(override val ctx: Context, channel: Channel<Message<T>>) : Pausable<T>(channel), Actor<T>, Controls {

    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    override val id: String get() { return ctx.id }


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
     * Serves as a hook for implementations to override for add custom diagnostics
     */
    protected open suspend fun track(source: String, data: Message<T>) {
    }
}
