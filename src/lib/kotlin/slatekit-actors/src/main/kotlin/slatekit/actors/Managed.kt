package slatekit.actors

import kotlinx.coroutines.channels.Channel

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Managed<T>(ctx: Context, channel: Channel<Message<T>>, enableStrictMode: Boolean = true)
    : Pausable<T>(ctx, channel, enableStrictMode), Actor<T> {


    /**
     * Sends a content message using the payload supplied
     * @param item : The payload to process
     */
    override suspend fun send(item: T): Receipt {
        return allow {
            channel.send(Content<T>(nextId(), data = item, reference = Message.NONE))
        }
    }


    /**
     * Sends a content message using the payload and target supplied
     * @param item  : The payload to process
     * @param reference: Target name which can be anything for implementing class
     */
    override suspend fun send(item: T, reference: String): Receipt {
        return allow {
            channel.send(Content<T>(nextId(), data = item, reference = reference))
        }
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
            is Control -> {
                state.handle(item.action)
            }
            is Content -> {
                state.begin(false)
                handle(item)
            }
            else -> {
                // Does not support Request<T>
            }
        }
    }


    /**
     * Implementing classes need to handle the work.
     */
    protected abstract suspend fun handle(item: Content<T>)
}
