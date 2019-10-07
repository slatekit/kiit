package slatekit.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import java.util.concurrent.atomic.AtomicReference


class Manager(all: List<Worker<*>>, val scheduler: Scheduler, val logger: Logger) {

    private val channel = Channel<JobRequest>()

    // TODO: Make settings configurable
    private val workers = Workers(channel, all, logger, DefaultScheduler(), 30)
    private val job = Job.Managed(all)
    private val status = AtomicReference<Status>(Status.InActive)


    /**
     * Requests an action on the entire job
     */
    suspend fun request(action: JobAction) {
        channel.send(JobRequest.TaskRequest(action))
    }


    /**
     * Requests an action on a specific worker
     */
    suspend fun request(action: JobAction, workerId: Identity) {
        channel.send(JobRequest.WorkRequest(action, workerId))
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    suspend fun manage(){
        for(request in channel){
            when(request) {
                // Affects the whole job/queue/workers
                is JobRequest.TaskRequest -> {
                    val action = request.action
                    when(action) {
                        is JobAction.Start   -> status.set(Status.Running)
                        is JobAction.Stop    -> status.set(Status.Stopped)
                        is JobAction.Pause   -> status.set(Status.Paused)
                        is JobAction.Resume  -> status.set(Status.Running)
                        is JobAction.Process -> status.set(Status.Running)
                        is JobAction.Delay   -> status.set(Status.Paused)
                        else                 -> {
                            logger.info("Unexpected state: ${request.action}")
                        }
                    }
                }
                // Affects just a specific worker
                is JobRequest.WorkRequest -> {
                    val action = request.action
                    val workerId = request.target
                    when(action) {
                        is JobAction.Start   -> workers.start(workerId)
                        is JobAction.Stop    -> workers.stop(workerId)
                        is JobAction.Pause   -> workers.pause(workerId)
                        is JobAction.Resume  -> workers.resume(workerId)
                        is JobAction.Process -> workers.process(workerId)
                        is JobAction.Delay   -> workers.start(workerId)
                        else                 -> {
                            logger.info("Unexpected state: ${request.action}")
                        }
                    }
                }
            }
        }
    }
}