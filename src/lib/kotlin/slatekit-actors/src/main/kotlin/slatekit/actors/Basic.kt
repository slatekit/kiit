package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicLong

/**
 * Simple base class for an Actor that supports sending and receiving
 * of content ( payload ) messages only
 */
abstract class Basic<T>(override val ctx: Context, protected val channel: Channel<Content<T>>) : Actor<T> {

    protected val idGen = AtomicLong(0L)


    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    override val id: String get() { return ctx.id }



    /**
     * Sends a payload to the actor
     * @param item  : Data / payload for message
     */
    override suspend fun send(item: T) {
        channel.send(Content(nextId(), item))
    }


    /**
     * Sends a payload with target to the actor
     * @param item  : Data / payload for message
     * @param reference: Optional, used as classifier to direct message to specific handler if enabled.
     */
    override suspend fun send(item:T, reference:String) {
        channel.send(Content(nextId(), item, reference = reference))
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
     * Gets the next id used in creating a new Message
     */
    protected fun nextId():Long = idGen.incrementAndGet()


    /**
     * Implementing classes need to handle the work.
     * The payload is inside the content
     */
    protected abstract suspend fun work(item: Content<T>)


    /**
     * This is for diagnostics, implementing classes track the messages
     */
    protected open suspend fun track(source: String, data: Content<T>) {
    }
}
