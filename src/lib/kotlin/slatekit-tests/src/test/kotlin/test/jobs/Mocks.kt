package test.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.jobs.support.Coordinator
import slatekit.jobs.support.JobId
import slatekit.jobs.support.Scheduler


class MockScheduler : Scheduler {
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}


open class MockCoordinator(override val logger: Logger, override val ids: JobId) : Coordinator {

    val requests = mutableListOf<Command>()

    override suspend fun request(jobRequest: Command) {
        requests.add(jobRequest)
    }

    override suspend fun respondOne(): Command? {
        return requests.firstOrNull()
    }


    override suspend fun respond(operation:suspend (Command) -> Unit ) {
        for(request in requests){
            operation(request)
        }
    }
}


class MockCoordinatorWithChannel(logger: Logger, ids: JobId, val channel: Channel<Command>) : MockCoordinator(logger, ids) {

    private var pauses = mutableListOf<Command>()


    override suspend fun request(request: Command){
//        logger.log(Info, "Coordinator: Adding", listOf(
//                "target" to request.target,
//                "id" to request.id.toString(),
//                "action" to request.action.name)
//        )
        if(request is Command.WorkerCommand && request.action == JobAction.Resume) {
            pauses.add(request)
        } else {
            send(request)
        }
    }


    override suspend fun respondOne(): Command? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (Command) -> Unit ) {
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


    private suspend fun send(request: Command){
        channel.send(request)
        requests.add(request)
    }
}