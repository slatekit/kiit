package slatekit.core.slatekit.core.actors

import kotlinx.coroutines.channels.Channel

class Control<T>(val channel:Channel<Message<T>>, val actor:Actor<T>) {
    suspend fun pull(count: Int = 1) {
        // Process X off the channel
        for (x in 1..count) {
            val item = channel.poll()
            item?.let {
                actor.track(PULL, it)
                actor.work(it)
            }
        }
    }


    suspend fun poll() {
        var item: Message<T>? = channel.poll()
        while (item != null) {
            actor.track(POLL, item)
            actor.work(item)
            item = channel.poll()
        }
    }


    suspend fun wipe() {
        var item: Message<T>? = channel.poll()
        while (item != null) {
            actor.track(WIPE, item)
            item = channel.poll()
        }
    }

    companion object {
        const val PULL = "PULL"
        const val POLL = "POLL"
        const val WORK = "WORK"
        const val WIPE = "WIPE"
        const val ERROR = "ERROR"
    }
}