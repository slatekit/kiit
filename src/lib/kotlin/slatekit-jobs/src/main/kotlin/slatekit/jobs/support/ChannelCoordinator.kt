package slatekit.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import slatekit.common.log.Logger

class ChannelCoordinator(override val logger: Logger, override val ids: JobId, val channel: Channel<Command>) : Coordinator {

    override suspend fun request(request: Command){
        channel.send(request)
    }


    override suspend fun respondOne(): Command? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (Command) -> Unit ) {
        for(request in channel){
            operation(request)
            yield()
        }
    }
}