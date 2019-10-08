package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import java.util.concurrent.atomic.AtomicReference


class JobManager(all: List<Worker<*>>, val scheduler: Scheduler, val logger: Logger) : Manager {

    private val channel = Channel<JobRequest>()

    // TODO: Make settings configurable
    private val workers = Workers(channel, all, logger, DefaultScheduler(), 30)
    override val job = Job.Managed(all)
    private val status = AtomicReference<Status>(Status.InActive)


    /**
     * Requests an action on the entire job
     */
    override suspend fun request(action: JobAction) {
        channel.send(JobRequest.TaskRequest(action))
    }


    /**
     * Requests an action on a specific worker
     */
    override suspend fun request(action: JobAction, workerId: Identity, desc:String?) {
        channel.send(JobRequest.WorkRequest(action, workerId, 0, desc))
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    override suspend fun manage(){
        for(request in channel){
            when(request) {
                // Affects the whole job/queue/workers
                is JobRequest.TaskRequest -> processJobRequest(request)

                // Affects just a specific worker
                is JobRequest.WorkRequest -> processWorkerRequest(request)
            }
        }
    }


    private suspend fun processJobRequest(request: JobRequest){
        val action = request.action
        when(action) {
            is JobAction.Start   -> start()
            is JobAction.Stop    -> stop()
            is JobAction.Resume  -> resume()
            is JobAction.Process -> process()
            is JobAction.Pause   -> pause(30)
            is JobAction.Delay   -> delayed(30)
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    private suspend fun processWorkerRequest(request: JobRequest.WorkRequest){
        val action = request.action
        val workerId = request.target
        when(action) {
            is JobAction.Start   -> perform { workers.start(workerId) }
            is JobAction.Stop    -> perform { workers.stop(workerId, request.desc) }
            is JobAction.Pause   -> perform { workers.pause(workerId, request.desc) }
            is JobAction.Resume  -> perform { workers.resume(workerId, request.desc) }
            is JobAction.Process -> perform { workers.process(workerId) }
            is JobAction.Delay   -> perform { workers.start(workerId) }
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    private suspend fun start()  = perform(Status.Running, JobAction.Start)
    private suspend fun stop()   = perform(Status.Stopped, JobAction.Stop)
    private suspend fun resume() = perform(Status.Running, JobAction.Resume)
    private suspend fun process() = perform(Status.Running, JobAction.Process)
    private suspend fun pause(seconds:Long) = perform(Status.Paused, JobAction.Resume, seconds)
    private suspend fun delayed(seconds:Long) = perform(Status.Paused, JobAction.Start, seconds)


    private suspend fun perform(newStatus:Status, action:JobAction, seconds:Long = 0) {
        status.set(newStatus)
        workers.workers.forEach {
            GlobalScope.launch {
                if(newStatus == Status.Paused){
                    workers.delay(it.id, seconds)
                } else {
                    channel.send(JobRequest.WorkRequest(action, it.id, seconds, ""))
                }
            }
        }
    }


    private suspend fun perform(operation:suspend() -> Unit){
        GlobalScope.launch {
            operation()
        }
    }
}