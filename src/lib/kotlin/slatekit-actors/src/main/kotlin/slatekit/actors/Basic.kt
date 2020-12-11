package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Base Actor that supports basic operations
 */
abstract class Basic<T>(override val ctx: Context, protected val channel: Channel<Content<T>>) : Actor<T> {

    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    override val id: String get() { return ctx.id }


    /**
     * Sends a message
     * @param msg  : Full message
     */
    override suspend fun send(msg: Content<T>) {
        channel.send(msg)
    }


    override suspend fun work(): Job {
        return ctx.scope.launch {
            for (msg in channel) {
                track(Puller.WORK, msg)
                work(msg)
                yield()
            }
        }
    }


    protected abstract suspend fun work(item: Content<T>)


    protected open suspend fun track(source: String, data: Content<T>) {
    }
}
