package slatekit.workers.slatekit.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.Status
import slatekit.common.log.Logger
import slatekit.jobs.JobAction
import slatekit.jobs.JobRequest
import slatekit.jobs.WorkState
import slatekit.jobs.Workable
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.builders.Tries


interface WorkLoop {
    val logger:Logger

    suspend fun loop(worker: Workable<*>, state: WorkState) {
        val result = Tries.attempt {
            when (state) {
                is WorkState.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkState.More -> {
                    request(JobRequest.WorkRequest(JobAction.Process, worker.id, 0, ""))
                }
            }
            ""
        }
        when (result) {
            is Success -> {
                println("ok")
            }
            is Failure -> {
                logger.error("Error while looping on : ${worker.id.fullName}")
            }
        }
    }


    suspend fun request(jobRequest: JobRequest)
}



class ChannelWorkLoop(override val logger: Logger, val channel: Channel<JobRequest>) : WorkLoop {

    override suspend fun request(request: JobRequest){
        channel.send(request)
    }
}