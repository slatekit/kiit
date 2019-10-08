package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import java.util.concurrent.atomic.AtomicReference


class JobManager(all: List<Worker<*>>, val scheduler: Scheduler, val logger: Logger) : Manager, StatusCheck{
    private val channel = Channel<JobRequest>()

    // TODO: Make settings configurable
    private val workers = Workers(channel, all, logger, DefaultScheduler(), 30)
    override val job = Job.Managed(all)
    private val _status = AtomicReference<Status>(Status.InActive)


    /**
     * Gets the current status of the job
     */
    override fun status(): Status = _status.get()


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
                is JobRequest.TaskRequest -> manageJob(request)

                // Affects just a specific worker
                is JobRequest.WorkRequest -> manageWorker(request)
            }
        }
    }


    /**
     * logs/handle error state/condition
     */
    override suspend fun error(currentStatus:Status, message:String) {
        val id = workers.workers.first()
        logger.error("Error with job ${id.id.name}: $message")
    }


    private suspend fun manageJob(request: JobRequest){
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


    private suspend fun manageWorker(request: JobRequest.WorkRequest){
        val action = request.action
        val workerId = request.target
        when(action) {
            is JobAction.Start   -> perform(action) { workers.start(workerId) }
            is JobAction.Stop    -> perform(action) { workers.stop(workerId, request.desc) }
            is JobAction.Pause   -> perform(action) { workers.pause(workerId, request.desc) }
            is JobAction.Resume  -> perform(action) { workers.resume(workerId, request.desc) }
            is JobAction.Process -> perform(action) { workers.process(workerId) }
            is JobAction.Delay   -> perform(action) { workers.start(workerId) }
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    private suspend fun start()               = transitionWorkers(JobAction.Start  , Status.Running)
    private suspend fun stop()                = transitionWorkers(JobAction.Stop   , Status.Stopped)
    private suspend fun resume()              = transitionWorkers(JobAction.Resume , Status.Running)
    private suspend fun process()             = transitionWorkers(JobAction.Process, Status.Running)
    private suspend fun pause(seconds:Long)   = transitionWorkers(JobAction.Resume , Status.Paused, seconds)
    private suspend fun delayed(seconds:Long) = transitionWorkers(JobAction.Start  , Status.Paused, seconds)


    private suspend fun perform(action: JobAction, operation:suspend() -> Unit){
        // Check state transition
        val currState = status()
        if(!WorkerUtils.validate(action, currState)) {
            val currentStatus = status()
            error(currentStatus, "Can not handle work while job is $currentStatus")
        }
        else {
            GlobalScope.launch {
                operation()
            }
        }
    }


    private suspend fun transitionWorkers(action:JobAction, newStatus:Status, seconds:Long = 0) {
        perform(action) {
            _status.set(newStatus)
            workers.workers.forEach {
                if (newStatus == Status.Paused) {
                    workers.delay(it.id, seconds)
                } else {
                    channel.send(JobRequest.WorkRequest(action, it.id, seconds, ""))
                }
            }
        }
    }
}