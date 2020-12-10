package slatekit.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

open class SimpleActor<T>(context: Context, channel: Channel<T>) : Base<T>(context, channel) {

    constructor(id:String, scope: CoroutineScope, channel: Channel<T>): this(Context(id, scope), channel)


    override suspend fun work(data: T) {

    }
}

