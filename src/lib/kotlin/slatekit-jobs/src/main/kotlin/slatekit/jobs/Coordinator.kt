package slatekit.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.Status
import slatekit.common.log.Logger
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.builders.Tries


interface Coordinator {
    val logger:Logger
    val ids:JobId

    suspend fun loop(worker: Workable<*>, state: WorkState) {
        val result = Tries.attempt {
            when (state) {
                is WorkState.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkState.More -> {
                    val id = ids.nextId()
                    val uuid = ids.nextUUID()
                    request(JobRequest.WorkRequest(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
                }
            }
            ""
        }
        when (result) {
            is Success -> {  }
            is Failure -> {
                logger.error("Error while looping on : ${worker.id.fullName}")
            }
        }
    }


    suspend fun request(jobRequest: JobRequest)


    suspend fun respondOne():JobRequest?


    suspend fun respond(operation:suspend (JobRequest) -> Unit )
}



class ChannelCoordinator(override val logger: Logger, override val ids:JobId, val channel: Channel<JobRequest>) : Coordinator {

    override suspend fun request(request: JobRequest){
        channel.send(request)
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