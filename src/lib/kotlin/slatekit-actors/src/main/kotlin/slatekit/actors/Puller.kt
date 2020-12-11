package slatekit.actors

import kotlinx.coroutines.channels.Channel

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
