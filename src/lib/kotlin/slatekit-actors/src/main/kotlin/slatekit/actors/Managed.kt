package slatekit.actors

import kotlinx.coroutines.channels.Channel

/**
 * Base class for an Actor that can be started, stopped, paused, and resumed
 */
abstract class Managed<T>(ctx: Context, channel: Channel<Message<T>>) : Pausable<T>(ctx, channel), Actor<T> {

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
}
