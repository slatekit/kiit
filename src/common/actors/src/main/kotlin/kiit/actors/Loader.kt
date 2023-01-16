package kiit.actors

import kotlinx.coroutines.channels.Channel

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Loader<T>(ctx: Context, channel: Channel<Message<T>>, enableStrictMode:Boolean = true)
    : Pausable<T>(ctx, channel, enableStrictMode) {


    /**
     * Request to load a payload internally into the channel
     */
    suspend fun load():Receipt {
        return allow {
            channel.send(Request(nextId(), reference = Message.NONE))
        }
    }


    /**
     * Request to load a payload internally into the channel, pay load is associated with the reference
     * @param reference  : Value to associate with the payload
     */
    suspend fun load(reference: String):Receipt {
        return allow {
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
                state.begin(false)
                handle(item)
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
