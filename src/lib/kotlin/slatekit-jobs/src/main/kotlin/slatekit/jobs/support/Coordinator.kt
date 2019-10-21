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

    suspend fun loop(worker: Workable<*>, state: WorkResult) {
        val result = Tries.attempt {
            when (state) {
                is WorkResult.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkResult.More -> {
                    val id = ids.nextId()
                    val uuid = ids.nextUUID()
                    request(Command.WorkerCommand(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
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


    suspend fun request(jobRequest: Command)


    suspend fun respondOne(): Command?


    suspend fun respond(operation:suspend (Command) -> Unit )
}



