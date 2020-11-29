package slatekit.core.common

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.yield
import slatekit.common.ids.Paired
import slatekit.common.log.Logger

class ChannelCoordinator<C>(override val logger: Logger, override val ids: Paired, val channel: Channel<C>) : Coordinator<C> {

    override fun sendSync(cmd:C) {
        channel.sendBlocking(cmd)
    }


    override suspend fun send(cmd: C) {
        channel.send(cmd)
    }

    override suspend fun poll(): C? {
        return channel.poll()
    }

    suspend fun stop():Boolean {
        return channel.close()
    }

    override suspend fun consume(operation: suspend (C) -> Unit) {
        for (cmd in channel) {
            operation(cmd)
            yield()
        }
    }
}