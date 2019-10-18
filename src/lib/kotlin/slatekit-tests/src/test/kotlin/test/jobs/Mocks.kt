package test.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.jobs.support.Coordinator
import slatekit.jobs.support.Scheduler


class MockScheduler : Scheduler {
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}


open class MockCoordinator(override val logger: Logger, override val ids: JobId) : Coordinator {

    val requests = mutableListOf<JobCommand>()

    override suspend fun request(jobRequest: JobCommand) {
        requests.add(jobRequest)
    }

    override suspend fun respondOne(): JobCommand? {
        return requests.firstOrNull()
    }


    override suspend fun respond(operation:suspend (JobCommand) -> Unit ) {
        for(request in requests){
            operation(request)
        }
    }
}


class MockCoordinatorWithChannel(logger: Logger, ids: JobId, val channel: Channel<JobCommand>) : MockCoordinator(logger, ids) {

    private var pauses = mutableListOf<JobCommand>()


    override suspend fun request(request: JobCommand){
//        logger.log(Info, "Coordinator: Adding", listOf(
//                "target" to request.target,
//                "id" to request.id.toString(),
//                "action" to request.action.name)
//        )
        if(request is JobCommand.ManageWorker && request.action == JobAction.Resume) {
            pauses.add(request)
        } else {
            send(request)
        }
    }


    override suspend fun respondOne(): JobCommand? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (JobCommand) -> Unit ) {
        for(request in channel){
            operation(request)
        }
    }


    suspend fun resume(){
        if(pauses.isNotEmpty()){
            val req = pauses.removeAt(pauses.lastIndex)
            send(req)
        }
    }


    private suspend fun send(request:JobCommand){
        channel.send(request)
        requests.add(request)
    }
}