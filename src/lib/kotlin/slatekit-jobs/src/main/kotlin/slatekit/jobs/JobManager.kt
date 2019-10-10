package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import java.util.concurrent.atomic.AtomicReference


class JobManager(all: List<Worker<*>>,
                 val coordinator: Coordinator,
                 val scheduler: Scheduler,
                 val logger: Logger) : Manager, StatusCheck{

    // TODO: Make settings configurable
    private val workers = Workers(all, coordinator, DefaultScheduler(), logger, 30)
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
        request(JobRequest.TaskRequest(action))
    }


    /**
     * Requests an action on a specific worker
     */
    override suspend fun request(action: JobAction, workerId: Identity, desc:String?) {
        request(JobRequest.work(action, workerId, 0L, desc))
    }


    /**
     * Requests an action on a specific worker
     */
    override suspend fun request(request: JobRequest) {
        coordinator.request(request)
    }


    /**
     * Listens to and handles 1 single request
     */
    override suspend fun respond() {
        val request = coordinator.respondOne()
        request?.let { manage(it) }
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    override suspend fun manage(){
        coordinator.respond { request ->
            manage(request)
        }
    }


    suspend fun manage(request: JobRequest){
        when(request) {
            // Affects the whole job/queue/workers
            is JobRequest.TaskRequest -> manageJob(request, true)

            // Affects just a specific worker
            is JobRequest.WorkRequest -> manageWorker(request, true)
        }
    }


    /**
     * logs/handle error state/condition
     */
    override suspend fun error(currentStatus:Status, message:String) {
        val id = workers.workers.first()
        logger.error("Error with job ${id.id.name}: $message")
    }


    suspend fun manageJob(request: JobRequest, launch:Boolean){
        val action = request.action
        when(action) {
            is JobAction.Start   -> start(launch)
            is JobAction.Stop    -> stop(launch)
            is JobAction.Resume  -> resume(launch)
            is JobAction.Process -> process(launch)
            is JobAction.Pause   -> pause(launch, 30)
            is JobAction.Delay   -> delayed(launch, 30)
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    suspend fun manageWorker(request: JobRequest.WorkRequest, launch:Boolean){
        val action = request.action
        val workerId = request.target
        when(action) {
            is JobAction.Start   -> perform(action, launch) { workers.start(workerId) }
            is JobAction.Stop    -> perform(action, launch) { workers.stop(workerId, request.desc) }
            is JobAction.Pause   -> perform(action, launch) { workers.pause(workerId, request.desc) }
            is JobAction.Resume  -> perform(action, launch) { workers.resume(workerId, request.desc) }
            is JobAction.Process -> perform(action, launch) { workers.process(workerId) }
            is JobAction.Delay   -> perform(action, launch) { workers.start(workerId) }
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    private suspend fun start(launch:Boolean)                 = transitionWorkers(JobAction.Start  , Status.Running, launch)
    private suspend fun stop(launch:Boolean)                  = transitionWorkers(JobAction.Stop   , Status.Stopped, launch)
    private suspend fun resume(launch:Boolean)                = transitionWorkers(JobAction.Resume , Status.Running, launch)
    private suspend fun process(launch:Boolean)               = transitionWorkers(JobAction.Process, Status.Running, launch)
    private suspend fun pause(launch:Boolean, seconds:Long)   = transitionWorkers(JobAction.Resume , Status.Paused, launch, seconds)
    private suspend fun delayed(launch:Boolean, seconds:Long) = transitionWorkers(JobAction.Start  , Status.Paused, launch, seconds)


    private suspend fun perform(action: JobAction, launch:Boolean, operation:suspend() -> Unit){
        // Check state transition
        val currState = status()
        if(!JobUtils.validate(action, currState)) {
            val currentStatus = status()
            error(currentStatus, "Can not handle work while job is $currentStatus")
        }
        else {
            if(launch) {
                GlobalScope.launch {
                    operation()
                }
            }
            else {
                operation()
            }
        }
    }


    private suspend fun transitionWorkers(action:JobAction, newStatus:Status, launch:Boolean, seconds:Long = 0) {
        perform(action, launch) {
            _status.set(newStatus)
            workers.workers.forEach {
                if (newStatus == Status.Paused) {
                    workers.delay(it.id, seconds)
                } else {
                    val req = JobRequest.WorkRequest(action, it.id, seconds, "")
                    coordinator.request(req)
                }
            }
        }
    }
}