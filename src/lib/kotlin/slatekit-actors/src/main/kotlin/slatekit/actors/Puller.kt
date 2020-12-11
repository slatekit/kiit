package slatekit.actors

import kotlinx.coroutines.channels.Channel

/**
 * Provides a way to extract messages out of an actor's channel,
 * and send them to the handler ( which could be the same actor ).
 * This is useful for controlling the content of the channel ( such as for tests )
 * and also for cleanup in some cases.
 * This allows to ull, poll, wipe ( clear ) messages from the channel.
 */
open class Puller<T>(val channel: Channel<Message<T>>,
                     val handler: Handler<T>, val tracker:((Message<T>) -> Unit)? = null) {

    suspend fun pull(count: Int = 1) {
        // Process X off the channel
        for (x in 1..count) {
            val item = channel.poll()
            item?.let {
                track(PULL, it)
                handler.handle(it)
            }
        }
    }


    suspend fun poll() {
        var item: Message<T>? = channel.poll()
        while (item != null) {
            track(POLL, item)
            handler.handle(item)
            item = channel.poll()
        }
    }


    suspend fun wipe() {
        var item: Message<T>? = channel.poll()
        while (item != null) {
            track(WIPE, item)
            item = channel.poll()
        }
    }


    suspend fun track(source: String, item: Message<T>) {
        tracker?.invoke(item)
    }


    companion object {
        const val PULL = "PULL"
        const val POLL = "POLL"
        const val WORK = "WORK"
        const val WIPE = "WIPE"
        const val ERROR = "ERROR"
    }
}
