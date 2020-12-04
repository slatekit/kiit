package test.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.ids.Paired
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.core.common.Scheduler


class MockScheduler : Scheduler {
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}


open class MockCoordinator(override val logger: Logger, override val ids: Paired) : slatekit.core.common.Coordinator<Command> {

    val requests = mutableListOf<Command>()

    override fun sendSync(cmd:Command) {
        requests.add(cmd)
    }

    override suspend fun send(jobRequest: Command) {
        requests.add(jobRequest)
    }

    override suspend fun poll(): Command? {
        return requests.firstOrNull()
    }

    override suspend fun consume(operation:suspend (Command) -> Unit ) {
        for(request in requests){
            operation(request)
        }
    }
}


class MockCoordinatorWithChannel(logger: Logger, ids: Paired, val channel: Channel<Command>) : MockCoordinator(logger, ids) {

    // To simulate scheduled pauses. e.g.
    private var pauses = mutableListOf<Command>()
    private var all = mutableListOf<Command>()


    override suspend fun send(request: Command){
//        logger.log(Info, "Coordinator: Adding", listOf(
//                "target" to request.target,
//                "id" to request.id.toString(),
//                "action" to request.action.name)
//        )
        all.add(request)
        if(request is Command.WorkerCommand && request.action == Action.Resume) {
            pauses.add(request)
        } else {
            sendInternal(request)
        }
    }


    override suspend fun poll(): Command? {
        return channel.receive()
    }


    override suspend fun consume(operation:suspend (Command) -> Unit ) {
        for(request in channel){
            operation(request)
        }
    }


    suspend fun resume(){
        if(pauses.isNotEmpty()){
            val req = pauses.removeAt(pauses.lastIndex)
            sendInternal(req)
        }
    }


    private suspend fun sendInternal(request: Command){
        channel.send(request)
        requests.add(request)
    }
}


