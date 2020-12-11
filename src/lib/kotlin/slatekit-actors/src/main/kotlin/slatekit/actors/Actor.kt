package slatekit.actors

import kotlinx.coroutines.Job

/**
 * Simple interface for an Actor
 */
interface Actor<T> {

    /**
     * Id of the actor, this can simply be a name like
     * 1. "emailer"
     * 2. {area}.{service} e.g. "signup.emailer"
     */
    val id:String

    /**
     * Contains relevant info about the actor
     */
    val ctx: Context

    /**
     * Sends a payload to the actor
     * @param item  : Data / payload for message
     */
    suspend fun send(item: T) {
        send(Content(item))
    }


    /**
     * Sends a payload with target to the actor
     * @param item  : Data / payload for message
     * @param target: Optional, used as classifier to direct message to specific handler if enabled.
     */
    suspend fun send(item:T, target:String) {
        send(Content(item, target = target))
    }

    suspend fun send(msg: Content<T>)

    suspend fun work(): Job
}
