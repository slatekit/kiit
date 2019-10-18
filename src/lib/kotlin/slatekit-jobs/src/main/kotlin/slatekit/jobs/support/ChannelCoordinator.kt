package slatekit.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import slatekit.common.log.Logger
import slatekit.jobs.JobId
import slatekit.jobs.JobCommand

class ChannelCoordinator(override val logger: Logger, override val ids: JobId, val channel: Channel<JobCommand>) : Coordinator {

    override suspend fun request(request: JobCommand){
        channel.send(request)
    }


    override suspend fun respondOne(): JobCommand? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (JobCommand) -> Unit ) {
        for(request in channel){
            operation(request)
            yield()
        }
    }
}