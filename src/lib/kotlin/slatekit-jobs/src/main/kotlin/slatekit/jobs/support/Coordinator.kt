package slatekit.jobs.support

import slatekit.common.Status
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.builders.Tries


interface Coordinator {
    val logger:Logger
    val ids: JobId

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
                    request(JobCommand.ManageWorker(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
                }
            }
            ""
        }
        when (result) {
            is Success -> {  }
            is Failure -> {
                logger.error("Error while looping on : ${worker.id.id}")
            }
        }
    }


    suspend fun request(jobRequest: JobCommand)


    suspend fun respondOne(): JobCommand?


    suspend fun respond(operation:suspend (JobCommand) -> Unit )
}



