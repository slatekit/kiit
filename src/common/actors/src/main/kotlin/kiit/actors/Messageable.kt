package kiit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicLong

/**
 * Base class for any @see[Actor]
 */
abstract class Messageable<T>(val ctx: Context, val channel: Channel<Message<T>>) : Workable {

    private val idGen = AtomicLong(0L)

    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    val id: String
        get() {
            return ctx.id
        }

    /**
     * Launches this actor to start processing messages.
     * This launches on the scope supplied in the context
     */
    override suspend fun work(): Job {
        return ctx.scope.launch {
            for (msg in channel) {
                track(Issuer.WORK, msg)
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
    protected abstract suspend fun work(item: Message<T>)

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
