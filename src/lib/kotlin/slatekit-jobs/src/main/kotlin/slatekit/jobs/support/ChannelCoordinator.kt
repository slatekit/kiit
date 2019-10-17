package slatekit.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import slatekit.common.log.Logger
import slatekit.jobs.JobId
import slatekit.jobs.JobRequest

class ChannelCoordinator(override val logger: Logger, override val ids: JobId, val channel: Channel<JobRequest>) : Coordinator {

    override suspend fun request(request: JobRequest){
        channel.send(request)
    }


    override suspend fun respondOne(): JobRequest? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (JobRequest) -> Unit ) {
        for(request in channel){
            operation(request)
            yield()
        }
    }
}