package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.common.ids.Identity
import slatekit.common.log.Info
import slatekit.common.log.Logger
import java.util.concurrent.atomic.AtomicReference


class JobManager(all: List<Worker<*>>,
                 val coordinator: Coordinator,
                 val scheduler: Scheduler,
                 val logger: Logger,
                 val ids:JobId = JobId()) : Manager, StatusCheck{

    // TODO: Make settings configurable
    val workers = Workers(all, coordinator, DefaultScheduler(), logger, ids, 30)
    val id = workers.all.first().id
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
        val id = ids.nextId()
        val uuid = ids.nextUUID()
        val req = JobRequest.TaskRequest(id, uuid.toString(), action)
        request(req)
    }


    /**
     * Requests an action on a specific worker
     */
    override suspend fun request(action: JobAction, workerId: Identity, desc:String?) {
        val id = ids.nextId()
        val uuid = ids.nextUUID()
        val req = JobRequest.WorkRequest(id, uuid.toString(), action, workerId, 30, desc)
        request(req)
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
        request?.let {
            runBlocking {
                manage(request, false)
            }
        }
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    override suspend fun manage(){
        coordinator.respond { request ->
            manage(request, true)
        }
    }


    suspend fun manage(request: JobRequest, launch:Boolean = true){
        when(request) {
            // Affects the whole job/queue/workers
            is JobRequest.TaskRequest -> {
                logger.log(Info, "Job: request - ", listOf("type" to "job", "id" to request.id.toString(), "uuid" to request.uuid.toString(), "action" to request.action.name), null)
                manageJob(request, launch)
            }

            // Affects just a specific worker
            is JobRequest.WorkRequest -> {
                logger.log(Info, "Job: request - ", listOf("type" to "wrk", "id" to request.id.toString(), "uuid" to request.uuid.toString(), "action" to request.action.name), null)
                manageWorker(request, launch)
            }
        }
    }


    /**
     * logs/handle error state/condition
     */
    override suspend fun error(currentStatus:Status, message:String) {
        val id = workers.all.first()
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
        val context = workers.get(workerId)
        when(context) {
            null -> { }
            else -> {
                val worker = context.worker
                val status = worker.status()
                when(action) {
                    is JobAction.Start   -> perform(action, status, launch) { workers.start(workerId) }
                    is JobAction.Stop    -> perform(action, status, launch) { workers.stop(workerId, request.desc) }
                    is JobAction.Pause   -> perform(action, status, launch) { workers.pause(workerId, request.desc) }
                    is JobAction.Resume  -> perform(action, status, launch) { workers.resume(workerId, request.desc) }
                    is JobAction.Process -> perform(action, status, launch) { workers.process(workerId) }
                    is JobAction.Delay   -> perform(action, status, launch) { workers.start(workerId) }
                    else                 -> {
                        logger.info("Unexpected state: ${request.action}")
                    }
                }
            }
        }
    }


    private suspend fun start(launch:Boolean)                 = transitionWorkers(JobAction.Start  , Status.Running, launch)
    private suspend fun stop(launch:Boolean)                  = transitionWorkers(JobAction.Stop   , Status.Stopped, launch)
    private suspend fun resume(launch:Boolean)                = transitionWorkers(JobAction.Resume , Status.Running, launch)
    private suspend fun process(launch:Boolean)               = transitionWorkers(JobAction.Process, Status.Running, launch)
    private suspend fun pause(launch:Boolean, seconds:Long)   = transitionWorkers(JobAction.Resume , Status.Paused, launch, seconds)
    private suspend fun delayed(launch:Boolean, seconds:Long) = transitionWorkers(JobAction.Start  , Status.Paused, launch, seconds)


    private suspend fun perform(action: JobAction, currentState:Status, launch:Boolean, operation:suspend() -> Unit){
        // Check state transition
        if(!JobUtils.validate(action, currentState)) {
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


    private val nameKey = "name" to this.id.name
    private suspend fun transitionWorkers(action:JobAction, newStatus:Status, launch:Boolean, seconds:Long = 0) {
        perform(action, status(), launch) {
            logger.log(Info, "Job:", listOf(nameKey, "transition" to newStatus.name))
            _status.set(newStatus)

            workers.all.forEach {
                if (newStatus == Status.Paused) {
                    workers.delay(it.id, seconds)
                } else {
                    val id = ids.nextId()
                    val uuid = ids.nextUUID()
                    val req = JobRequest.WorkRequest(id, uuid.toString(), action, it.id, seconds, "")
                    logger.log(Info, "Job:", listOf(nameKey, "target" to req.target.fullName, "action" to req.action.name))
                    coordinator.request(req)
                }
            }
        }
    }
}