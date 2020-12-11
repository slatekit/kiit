package slatekit.actors

import kotlinx.coroutines.Job

interface Actor<T> {

    val id:String

    val ctx: Context

    /**
     * Sends a content message with target
     * @param item  : Data / payload for message
     */
    suspend fun send(item: T) {
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

    suspend fun send(msg: Content<T>)

    suspend fun work(): Job
}
