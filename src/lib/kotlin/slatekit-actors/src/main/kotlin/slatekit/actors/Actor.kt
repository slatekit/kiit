package slatekit.actors

/**
 * Simple interface for an Actor
 */
interface Actor<T> : Workable {

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
    suspend fun send(item: T)

    /**
     * Sends a payload with target to the actor
     * @param item  : Data / payload for message
     * @param reference: Optional value to associate with the item
     */
    suspend fun send(item:T, reference:String)
}
