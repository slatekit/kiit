package test.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.log.Logger
import slatekit.jobs.*


class MockScheduler : Scheduler{
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}


open class MockCoordinator(override val logger: Logger, override val ids: JobId) : Coordinator {

    val requests = mutableListOf<JobRequest>()

    override suspend fun request(jobRequest: JobRequest) {
        requests.add(jobRequest)
    }

    override suspend fun respondOne(): JobRequest? {
        return requests.firstOrNull()
    }


    override suspend fun respond(operation:suspend (JobRequest) -> Unit ) {
        for(request in requests){
            operation(request)
        }
    }
}


class MockCoordinatorWithChannel(logger: Logger, ids: JobId, val channel: Channel<JobRequest>) : MockCoordinator(logger, ids) {


    override suspend fun request(request: JobRequest){
        channel.send(request)
        requests.add(request)
    }


    override suspend fun respondOne(): JobRequest? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (JobRequest) -> Unit ) {
        for(request in channel){
            operation(request)
        }
    }
}