package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.StatusCheck
import slatekit.common.log.Info
import slatekit.common.log.Logger
import slatekit.common.log.LoggerConsole
import slatekit.jobs.events.Events
import slatekit.jobs.events.JobEvents
import slatekit.jobs.support.*
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * A Job is the top level model in this Background Job/Task Queue system. A job is composed of the following:
 *
 * TERMS:
 * 1. Identity  : @see[slatekit.common.Identity] to distinctly identify a job
 * 2. Queue     : Optional Queue ( containing @see[slatekit.jobs.Task]s that workers can work on
 * 3. Task      : A single work item ( from a queue ) that a worker can work on
 * 4. Workers   : 1 or more @see[slatekit.jobs.Worker]s that can work on this job
 * 5. Manage    : Operations to manage ( start | stop | pause | resume | delay ) a job or individual worker
 * 6. Events    : Subscribing to events on the job/worker ( only status changes for now )
 * 7. Stats     : Reasonable statistics / diagnostics for workers such as total calls, processed, logging
 * 8. Policies  : @see[slatekit.functions.policy.Policy] associated with a worker such as limits, retries
 *
 * NOTES:
 * 1. Coordination is kept thread safe as all requests to manage / control a job are handled via channels
 * 2. A worker does not need to work from a Task Queue
 * 3. You can stop / pause / resume the whole job which means all workers are paused
 * 4. You can stop / pause / resume a single worker also
 * 5. Policies such as limiting the amount of runs, processed work, error rates, retries are done via policies
 * 6. The identity of a worker is based on the identity of its parent job + a workers uuid/unique
 *
 * INSPIRED BY:
 * 1. Inspired by Ruby Rails SideKiq, NodesJS Bull, Python Celery
 * 2. Kotlin Structured Concurrency via Channels ( misc blog posts by Kotlin Team )
 * 3. Actor model from other languages
 *
 *
 * LIMITS:
 * 1. Only support queues from slatekit ( which is abstracted ) with implementations for AWS SQS
 * 2. No database support ( e.g. for storing status/state/results in the database, etc )
 * 3. No distributed jobs support
 *
 *
 * FUTURE:
 * 1. Address the limits listed above
 * 2. Improve events/subscriptions
 * 3. Integration with Kotlin Flow ( e.g. a job could feed data into a Flow )
 *
 */
class Job(val id:Identity,
          all: List<Worker<*>>,
          val queue: Queue? = null,
          val logger: Logger = LoggerConsole(),
          val ids: JobId = JobId(),
          val coordinator: Coordinator = coordinator(ids, logger),
          val scheduler: Scheduler = DefaultScheduler()) : Management, StatusCheck, Events<Job> {
    /**
     * Initialize with just a function that will handle the work
     */
    constructor(id:Identity, lambda: suspend () -> WorkResult, queue: Queue? = null) : this(id, listOf(worker(lambda)), queue)


    /**
     * Initialize with just a function that will handle the work
     */
    constructor(id:Identity, lambda: suspend (Task) -> WorkResult, queue: Queue? = null) : this(id, listOf(lambda), queue)


    /**
     * Initialize with a list of functions to excecute work
     */
    constructor(id:Identity, lambdas: List<suspend (Task) -> WorkResult>, queue: Queue? = null) : this(id, workers(id, lambdas), queue)


    val workers = Workers(id, all, coordinator, scheduler, logger, ids, 30)
    private val events: Events<Job> = JobEvents()
    private val dispatch = JobDispatch(this, workers, events as JobEvents)
    private val _status = AtomicReference<Status>(Status.InActive)


    /**
     * Subscribe to @see[slatekit.common.Status] being changed
     */
    override suspend fun subscribe(op: suspend (Job) -> Unit) {
        events.subscribe(op)
    }


    /**
     * Subscribe to @see[slatekit.common.Status] beging changed to the one supplied
     */
    override suspend fun subscribe(status: Status, op: suspend (Job) -> Unit) {
        events.subscribe(status, op)
    }


    /**
     * Gets the current @see[slatekit.common.Status] of the job
     */
    override fun status(): Status = _status.get()


    /**
     * Run the job by starting it first and then managing it by listening for requests
     */
    override suspend fun run() {
        start()
        // TODO: Use a different scope
        GlobalScope.launch {
            manage()
        }
    }


    /**
     * Requests this job to perform the supplied command
     */
    override suspend fun request(command: Command) {
        // Coordinator handles requests via kotlin channels
        coordinator.request(command)
    }


    /**
     * Listens to and handles 1 single request
     */
    override suspend fun respond() {
        // Coordinator takes 1 request off the channel
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


    suspend fun manage(request: Command, launch:Boolean = true){
        logger.log(Info, "Job: request - ", request.pairs(), null)
        when(request) {
            // Affects the whole job/queue/workers
            is Command.JobCommand -> {
                manageJob(request, launch)
            }

            // Affects just a specific worker
            is Command.WorkerCommand -> {
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


    /**
     * Gets the next pair of ids
     */
    override fun nextIds():Pair<Long, UUID> = Pair(ids.nextId(), ids.nextUUID())


    internal fun setStatus(newStatus:Status){
        _status.set(newStatus)
    }


    private suspend fun manageJob(request: Command, launch:Boolean){
        val action = request.action
        when(action) {
            is JobAction.Start   -> dispatch.start(launch)
            is JobAction.Stop    -> dispatch.stop(launch)
            is JobAction.Resume  -> dispatch.resume(launch)
            is JobAction.Process -> dispatch.process(launch)
            is JobAction.Pause   -> dispatch.pause(launch, 30)
            is JobAction.Delay   -> dispatch.delayed(launch, 30)
            else                 -> {
                logger.info("Unexpected state: ${request.action}")
            }
        }
    }


    private suspend fun manageWorker(request: Command.WorkerCommand, launch:Boolean){
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
                    is JobAction.Start   -> JobUtils.perform(this, action, status, launch) { workers.start(workerId, task) }
                    is JobAction.Stop    -> JobUtils.perform(this, action, status, launch) { workers.stop(workerId, request.desc) }
                    is JobAction.Pause   -> JobUtils.perform(this, action, status, launch) { workers.pause(workerId, request.desc) }
                    is JobAction.Process -> JobUtils.perform(this, action, status, launch) { workers.process(workerId, task) }
                    is JobAction.Resume  -> JobUtils.perform(this, action, status, launch) { workers.resume(workerId, request.desc, task) }
                    is JobAction.Delay   -> JobUtils.perform(this, action, status, launch) { workers.start(workerId) }
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


    companion object {

        fun worker(call:suspend() -> WorkResult) : suspend(Task) -> WorkResult {
            return { t ->
                call()
            }
        }

        fun workers(id:Identity, lamdas:List<suspend(Task) -> WorkResult>) :List<Worker<*>>{
            return lamdas.map {
                Worker<String>(id, operation = it )
            }
        }


        fun coordinator(ids:JobId, logger: Logger):Coordinator {
            return ChannelCoordinator(logger, ids, Channel())
        }
    }
}