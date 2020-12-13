package slatekit.actors

import kotlinx.coroutines.channels.Channel

/**
 * Simple base class for an Actor that supports sending and receiving of content ( payload ) messages only
 */
abstract class Basic<T>(ctx: Context, channel: Channel<Message<T>>)
    : Messageable<T>(ctx, channel), Actor<T> {


    /**
     * Sends a payload to the actor
     * @param item  : Data / payload for message
     */
    override suspend fun send(item: T) {
        channel.send(Content(nextId(), item, reference = Message.NONE))
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
     *  Handles each message based on its type @see[Content], @see[Control],
     *  This handles following message types and moves this actor to a running state correctly
     *  1. @see[Control] messages to start, stop, pause, resume the actor
     *  2. @see[Request] messages to load payloads from a source ( e.g. queue )
     *  3. @see[Content] messages are simply delegated to the work method
     */
    override suspend fun work(item: Message<T>) {
        when (item) {
            is Content -> {
                handle(item)
            }
            is Control -> {
                // Does not support Control<T>
            }
            else -> {
                // Does not support Content<T>
            }
        }
    }


    /**
     * Implementing classes need to handle the work.
     */
    protected abstract suspend fun handle(item: Content<T>)
}
