package slatekit.jobs.workers

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.ids.Paired
import slatekit.common.log.LogLevel
import slatekit.common.log.Logger
import slatekit.common.paged.Pager
import slatekit.core.common.Scheduler
import slatekit.core.common.Emitter
import slatekit.jobs.Event
import slatekit.jobs.Action
import slatekit.jobs.Task
import slatekit.tracking.Recorder
import slatekit.policy.Policy
import slatekit.jobs.slatekit.jobs.support.Backoffs
import slatekit.jobs.support.*
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

/**
 * Represents a cluster of Workers that are affiliated with 1 job.
 */
class Workers(
    val jobId: Identity,
    val all: List<Worker<*>>,
    val coordinator: slatekit.core.common.Coordinator<Command>,
    val scheduler: Scheduler,
    val logger: Logger,
    val ids: Paired,
    val pauseInSeconds: Long,
    val policies: List<Policy<WorkRequest, WorkResult>> = listOf(),
    val backoffs: () -> Pager<Long> = { Backoffs.times() }
)  {

    private val events = Emitter<Event.WorkerEvent>()
    private val lookup: Map<String, Executor> = all.map { it.id.id to WorkerContext(jobId, it, Recorder.of(it.id), Backoffs(backoffs()), policies) }
            .map { it.first to Executor.of(it.second) }.toMap()

    /**
     * Subscribe to status being changed for any worker
     */
    suspend fun subscribe(op: suspend (Event.WorkerEvent) -> Unit) {
        events.on(op)
    }

    /**
     * Subscribe to status beging changed to the one supplied for any worker
     */
    suspend fun subscribe(status: Status, op: suspend (Event.WorkerEvent) -> Unit) {
        events.on(status.name, op)
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(id: Identity): WorkerContext? = when (lookup.containsKey(id.id)) {
        true -> lookup[id.id]?.context
        false -> null
    }

    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(id: String): WorkerContext? = when (lookup.containsKey(id)) {
        true -> lookup[id]?.context
        false -> null
    }

    /**
     * Gets all the worker ids
     */
    fun getIds(): List<Identity> = all.map { it.id }

    /**
     * Starts the worker associated with the identity and makes it work using the supplied Task
     */
    suspend fun start(id: Identity, task: Task = Task.empty, requireTask:Boolean = false) {
        perform(null,"Starting", id) { executor ->
            val context = executor.context
            val worker = context.worker
            val result = Runner.record(context) {
                val result: Try<WorkResult> = Runner.attemptStart(worker, false, true, task, requireTask) {
                    notify(context, it.status().name)
                }
                result.toOutcome()
            }.inner()

            when (result) {
                is Success -> {
                    val res = result.value
                    loop(context, res)
                    worker.status()
                }
                is Failure -> {
                    logger.error("Unable to start worker ${id.id}")
                    Status.Failed
                }
            }
            Outcomes.success(Status.Running)
        }
    }

    suspend fun process(id: Identity, task: Task = Task.empty) {
        perform(null, "Processing", id) { executor ->
            val context = executor.context
            val result = executor.execute(task)
            result.map { res -> loop(context, res) }
            Outcomes.success(Status.Running)
        }
    }

    suspend fun resume(id: Identity, reason: String?, task: Task = Task.empty) {
        perform(Status.Running, reason, id) { executor ->
            val context = executor.context
            val result = executor.resume(reason ?: "Resuming", task)
            result.map { res -> loop(context, res) }
            Outcomes.success(Status.Running)
        }
    }

    suspend fun pause(id: Identity, reason: String?, seconds: Long? = null) {
        perform(Status.Paused, reason, id) { executor ->
            val context = executor.context
            val worker = context.worker
            worker.pause(reason ?: "Paused")
            val pauseInSecs = seconds ?: pauseInSeconds
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSecs)) {
                coordinator.send(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), Action.Resume, worker.id, 0, null))
            }
            Outcomes.success(Status.Paused)
        }
    }

    suspend fun backoff(id: Identity, reason: String?) {
        perform(Status.Paused, reason ?: "Backoff", id) { executor ->
            val context = executor.context
            val worker = context.worker
            worker.pause(reason ?: "Backoff")
            val pauseInSecs = context.backoffs.next()
            record(worker.id, "pause_start", listOf("seconds" to pauseInSecs.toString()))
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSecs)) {
                record(worker.id, "pause_finish")
                coordinator.send(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), Action.Resume, worker.id, 0, null))
            }
            Outcomes.success(Status.Paused)
        }
    }

    suspend fun stop(id: Identity, reason: String?) {
        perform(Status.Stopped, reason, id) { executor ->
            val context = executor.context
            val worker = context.worker
            worker.stop(reason ?: "Stopped")
            Outcomes.success(Status.Stopped)
        }
    }

    suspend fun delay(id: Identity, seconds: Long) {
        record(id, "delay", listOf("seconds" to seconds.toString()))
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            coordinator.send(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), Action.Start, id, 0, null))
        }
    }

    private suspend fun perform(status: Status?, reason: String?, id: Identity, operation: suspend (Executor) -> Outcome<Status>): Outcome<Status> {
        record(id, status?.name ?: reason ?: "")
        val executor = this.lookup[id.id]
        return when (executor) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                val context = executor.context
                status?.let {
                    context.worker.move(status, reason)
                    notify(context, status.name)
                }
                operation(executor)
            }
        }
    }

    private suspend fun loop(context: WorkerContext, workResult: WorkResult) {
        val worker: Worker<*> = context.worker
        val result = Tries.of {
            when (workResult) {
                is WorkResult.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.move(Status.Complete)
                    worker.done()
                    notify(context, "Done")
                }
                is WorkResult.Next -> {
                    val (id, uuid) = ids.next()
                    coordinator.send(Command.WorkerCommand(id, uuid.toString(), Action.Process, worker.id, 0, ""))
                }
                is WorkResult.More -> {
                    val (id, uuid) = ids.next()
                    coordinator.send(Command.WorkerCommand(id, uuid.toString(), Action.Process, worker.id, 0, ""))
                }
            }
            ""
        }
        when (result) {
            is Success -> { }
            is Failure -> {
                logger.error("Error while looping on : ${worker.id.id}")
            }
        }
    }


    private fun record(id:Identity, action:String, extra:List<Pair<String,String>> = listOf()){
        val pairs = listOf("id" to id.id, "action" to action) + extra
        logger.log(LogLevel.Info, "Workers:", pairs)
    }

    private suspend fun notify(context: WorkerContext, action:String) {
        try {
            val worker = context.worker
            val task = context.task
            record(worker.id, action, task.structured())
            val event = Event.WorkerEvent(worker.id, worker.status(), worker.info())

            events.emit(event)
            events.emit(event.status.name, event)
        }
        catch(ex:Exception){
        }
    }
}
