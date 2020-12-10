package slatekit.actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

abstract class Base<T>(private val context:Context,
                       protected val channel: Channel<Message<T>>) : Actor<T> {

    override val id:String = context.id


    override suspend fun send(item:T) {
        channel.send(item)
    }


    override suspend fun work() {
        context.scope.launch {
            for (msg in channel) {
                track(Control.WORK, msg)
                work(msg)
                yield()
            }
        }
    }


    protected open suspend fun track(source:String, data: T) {
    }
}
