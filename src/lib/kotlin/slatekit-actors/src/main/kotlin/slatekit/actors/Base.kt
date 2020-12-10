package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Base Actor that supports basic operations
 */
abstract class Base<T>(protected val context: Context,
                       protected val channel: Channel<Message<T>>) : Actor<T> {

    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    override val id: String = context.id


    /**
     * Sends a content message with target
     * @param item  : Data / payload for message
     */
    override suspend fun send(item: T) {
        send(Content(item))
    }


    /**
     * Sends a content message with target
     * @param item  : Data / payload for message
     * @param target: Optional, used as classifier to direct message to specific handler if enabled.
     */
    suspend fun send(item:T, target:String) {
        send(Content(item, target = target))
    }


    /**
     * Sends a message
     * @param msg  : Full message
     */
    suspend fun send(msg: Message<T>) {
        channel.send(msg)
    }


    override suspend fun work(): Job {
        return context.scope.launch {
            for (msg in channel) {
                track(Remover.WORK, msg)
                work(msg)
                yield()
            }
        }
    }


    protected open suspend fun track(source: String, data: Message<T>) {
    }
}
