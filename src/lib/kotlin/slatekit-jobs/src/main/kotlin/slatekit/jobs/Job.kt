package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.common.Identity
import slatekit.common.log.Info
import slatekit.common.log.Logger
import slatekit.jobs.events.Events
import slatekit.jobs.events.JobEvents
import slatekit.jobs.support.Coordinator
import slatekit.jobs.support.JobId
import slatekit.jobs.support.JobUtils
import slatekit.jobs.support.Scheduler
import java.util.concurrent.atomic.AtomicReference


class Job(all: List<Worker<*>>,
          val queue: Queue?,
          val coordinator: Coordinator,
          val scheduler: Scheduler,
          val logger: Logger,
          val ids: JobId = JobId()) : Manager, StatusCheck, Events<Job> {


    val workers = Workers(all, coordinator, scheduler, logger, ids, 30)
    val id = workers.all.first().id
    private val events: Events<Job> = JobEvents()
    private val _status = AtomicReference<Status>(Status.InActive)


    override suspend fun subscribe(op: suspend (Job) -> Unit) {
        events.subscribe(op)
    }


    override suspend fun subscribe(status: Status, op: suspend (Job) -> Unit) {
        events.subscribe(status, op)
    }


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
        val req = JobCommand.ManageJob(id, uuid.toString(), action)
        request(req)
    }


    /**
     * Requests an action on a specific worker
     */
    override suspend fun request(action: JobAction, workerId: Identity, desc:String?) {
        val id = ids.nextId()
        val uuid = ids.nextUUID()
        val req = JobCommand.ManageWorker(id, uuid.toString(), action, workerId, 30, desc)
        request(req)
    }


    /**
     * Requests an action on a specific worker
     */
    override suspend fun request(request: JobCommand) {
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


    suspend fun manage(request: JobCommand, launch:Boolean = true){
        logger.log(Info, "Job: request - ", request.pairs(), null)
        when(request) {
            // Affects the whole job/queue/workers
            is JobCommand.ManageJob -> {
                manageJob(request, launch)
            }

            // Affects just a specific worker
            is JobCommand.ManageWorker -> {
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


    suspend fun manageJob(request: JobCommand, launch:Boolean){
        val action = request.action
        when(action) {
            is JobAction.Start   -> onStart(launch)
            is JobAction.Stop    -> onStop(launch)
            is JobAction.Resume  -> onResume(launch)
            is JobAction.Process -> onProcess(launch)
            is JobAction.Pause   -> onPause(launch, 30)
            is JobAction.Delay   -> onDelayed(launch, 30)
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    suspend fun manageWorker(request: JobCommand.ManageWorker, launch:Boolean){
        val action = request.action
        val workerId = request.workerId
        val context = workers.get(workerId)
        when(context) {
            null -> { }
            else -> {
                val worker = context.worker
                val status = worker.status()
                val task = nextTask()
                when(action) {
                    is JobAction.Start   -> perform(action, status, launch) { workers.start(workerId, task) }
                    is JobAction.Stop    -> perform(action, status, launch) { workers.stop(workerId, request.desc) }
                    is JobAction.Pause   -> perform(action, status, launch) { workers.pause(workerId, request.desc) }
                    is JobAction.Process -> perform(action, status, launch) { workers.process(workerId, task) }
                    is JobAction.Resume  -> perform(action, status, launch) { workers.resume(workerId, request.desc, task) }
                    is JobAction.Delay   -> perform(action, status, launch) { workers.start(workerId) }
                    else                 -> {
                        logger.info("Unexpected state: ${request.action}")
                    }
                }
            }
        }
    }


    private fun nextTask(): Task {
        val task = when(queue) {
            null -> Task.empty
            else -> {
                val entry = queue.queue.next()
                entry?.let { Task(it, queue) } ?: Task.empty
            }
        }
        return task
    }


    private suspend fun onStart(launch:Boolean)                 = transitionWorkers(JobAction.Start  , Status.Running, launch)
    private suspend fun onStop(launch:Boolean)                  = transitionWorkers(JobAction.Stop   , Status.Stopped, launch)
    private suspend fun onResume(launch:Boolean)                = transitionWorkers(JobAction.Resume , Status.Running, launch)
    private suspend fun onProcess(launch:Boolean)               = transitionWorkers(JobAction.Process, Status.Running, launch)
    private suspend fun onPause(launch:Boolean, seconds:Long)   = transitionWorkers(JobAction.Pause  , Status.Paused, launch, seconds)
    private suspend fun onDelayed(launch:Boolean, seconds:Long) = transitionWorkers(JobAction.Start  , Status.Paused, launch, seconds)


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
            //logger.log(Info, "Job:", listOf(nameKey, "transition" to newStatus.name))
            _status.set(newStatus)
            val job = this

            GlobalScope.launch {
                (job.events as JobEvents).notify(job)
            }

            workers.all.forEach {
                val id = ids.nextId()
                val uuid = ids.nextUUID()
                val req = JobCommand.ManageWorker(id, uuid.toString(), action, it.id, seconds, "")
                //logger.log(Info, "Job:", listOf(nameKey, "target" to req.target.id, "action" to req.action.name))
                coordinator.request(req)
            }
        }
    }
}