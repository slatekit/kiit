package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.log.LogLevel
import slatekit.common.log.Logger
import slatekit.tracking.Recorder
import slatekit.policy.Policy
import slatekit.jobs.events.Events
import slatekit.jobs.events.WorkerEvents
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
    val coordinator: Coordinator,
    val scheduler: Scheduler,
    val logger: Logger,
    val ids: JobId,
    val pauseInSeconds: Long,
    val policies: List<Policy<WorkRequest, WorkResult>> = listOf()
) : Events<Worker<*>> {

    private val events: Events<Worker<*>> = WorkerEvents(this)
    private val lookup: Map<String, WorkExecutor> = all.map { it.id.id to WorkerContext(jobId, it, Recorder.of(it.id), Backoffs(), policies) }
            .map { it.first to WorkExecutor.of(it.second) }.toMap()

    /**
     * Subscribe to status being changed for any worker
     */
    override suspend fun subscribe(op: suspend (Worker<*>) -> Unit) {
        events.subscribe(op)
    }

    /**
     * Subscribe to status beging changed to the one supplied for any worker
     */
    override suspend fun subscribe(status: Status, op: suspend (Worker<*>) -> Unit) {
        events.subscribe(status, op)
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
    suspend fun start(id: Identity, task: Task = Task.empty) {
        perform("Starting", id) { executor ->
            val context = executor.context
            val worker = context.worker
            val result = Runner.record(context) {
                val result: Try<WorkResult> = Runner.attemptStart(worker, false, true, task) {
                    notify(context)
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
        perform("Processing", id) { executor ->
            val context = executor.context
            val result = executor.execute(task)
            result.map { res -> loop(context, res) }
            Outcomes.success(Status.Running)
        }
    }

    suspend fun resume(id: Identity, reason: String?, task: Task = Task.empty) {
        performPausableAction(Status.Running, id) { executor, pausable ->
            val context = executor.context
            val result = executor.resume(reason ?: "Resuming", task)
            result.map { res -> loop(context, res) }
            Outcomes.success(Status.Running)
        }
    }

    suspend fun pause(id: Identity, reason: String?, seconds: Long? = null) {
        performPausableAction(Status.Paused, id) { executor, pausable ->
            val context = executor.context
            val worker = context.worker
            pausable.pause(reason ?: "Paused")
            val pauseInSecs = seconds ?: pauseInSeconds
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSecs)) {
                coordinator.send(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), JobAction.Resume, worker.id, 0, ""))
            }
            Outcomes.success(Status.Paused)
        }
    }

    suspend fun stop(id: Identity, reason: String?) {
        performPausableAction(Status.Stopped, id) { worker, pausable ->
            pausable.stop(reason ?: "Stopped")
            Outcomes.success(Status.Stopped)
        }
    }

    suspend fun delay(id: Identity, seconds: Long) {
        logger.log(LogLevel.Info, "Worker:", listOf("id" to id.name, "action" to "delaying", "seconds" to "$seconds"))
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            coordinator.send(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), JobAction.Start, id, 0, ""))
        }
    }

    private suspend fun perform(action: String, id: Identity, operation: suspend (WorkExecutor) -> Outcome<Status>): Outcome<Status> {
        logger.log(LogLevel.Info, "Worker:", listOf("id" to id.name, "action" to action))
        val executor = this.lookup[id.id]
        return when (executor) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                val result = operation(executor)
                result
            }
        }
    }

    private suspend fun performPausableAction(status: Status, id: Identity, operation: suspend (WorkExecutor, Pausable) -> Outcome<Status>): Outcome<Status> {
        logger.log(LogLevel.Info, "Worker:", listOf("id" to id.name, "move" to status.name))
        val executor = this.lookup[id.id]
        return when (executor) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                val context = executor.context
                when (context.worker) {
                    is Pausable -> {
                        context.worker.move(status)
                        notify(context)
                        operation(executor, context.worker)
                    }
                    else -> Outcomes.errored("${context.worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action")
                }
            }
        }
    }

    private suspend fun loop(context: WorkerContext, workResult: WorkResult) {
        val worker: Workable<*> = context.worker
        val result = Tries.of {
            when (workResult.state) {
                is WorkState.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.move(Status.Complete)
                    worker.done()
                    notify(context)
                }
                is WorkState.Next -> {
                    val (id, uuid) = ids.next()
                    coordinator.send(Command.WorkerCommand(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
                }
                is WorkState.More -> {
                    val (id, uuid) = ids.next()
                    coordinator.send(Command.WorkerCommand(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
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

    private suspend fun notify(context: WorkerContext) {
        (events as WorkerEvents).notify(context.worker)
    }
}
