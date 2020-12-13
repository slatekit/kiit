package slatekit.actors

import kotlinx.coroutines.channels.Channel

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Producer<T>(ctx: Context, channel: Channel<Message<T>>) : Pausable<T>(ctx, channel) {


    /**
     * Request to produce a payload internally ( say from a queue ) and process it
     * @param msg  : Full message
     */
    suspend fun request() {
        allow {
            channel.send(Request(nextId(), reference = Message.NONE))
        }
    }


    /**
     * Sends a request message associated with the supplied target to produce a payload
     * This represents a request to get payload internally ( say from a queue ) and process it
     * @param reference  : Full message
     */
    suspend fun request(reference: String) {
        allow {
            channel.send(Request(nextId(), reference = reference))
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
            is Request -> {
                state.begin(); handle(item)
            }
            else -> {
                // Does not support Request<T>
            }
        }
    }


    /**
     * Handles a request for to produce a payload and dispatches
     */
    protected abstract suspend fun handle(req: Request<T>)
}
