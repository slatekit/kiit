package slatekit.jobs.workers

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.log.LogLevel
import slatekit.common.log.Logger
import slatekit.jobs.Event
import slatekit.jobs.Action
import slatekit.jobs.Task
import slatekit.tracking.Recorder
import slatekit.core.common.Backoffs
import slatekit.jobs.support.*
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

/**
 * Represents a cluster of Workers that are affiliated with 1 job.
 */
class Workers(val ctx: JobContext) {
    private val PAUSE_IN_SECONDS = 30L
    private val events = ctx.notifier.wrkEvents
    private val lookup: Map<String, Executor> = all.map { it.id.id to WorkerContext(id, it, Recorder.of(it.id), Backoffs(ctx.backoffs), ctx.policies) }
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
    suspend fun start(id: Identity, task: Task = Task.empty, requireTask: Boolean = false) {
        perform(null, "Starting", id) { executor ->
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
                    ctx.logger.error("Unable to start worker ${id.id}")
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
            val pauseInSecs = seconds ?: PAUSE_IN_SECONDS
            ctx.scheduler.schedule(DateTime.now().plusSeconds(pauseInSecs)) {
                ctx.channel.send(Command.WorkerCommand(ctx.ids.nextId(), ctx.ids.nextUUID().toString(), Action.Resume, worker.id, 0, null))
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
            ctx.scheduler.schedule(DateTime.now().plusSeconds(pauseInSecs)) {
                record(worker.id, "pause_finish")
                ctx.channel.send(Command.WorkerCommand(ctx.ids.nextId(), ctx.ids.nextUUID().toString(), Action.Resume, worker.id, 0, null))
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
        ctx.scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            ctx.channel.send(Command.WorkerCommand(ctx.ids.nextId(), ctx.ids.nextUUID().toString(), Action.Start, id, 0, null))
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
                    ctx.logger.info("Worker ${worker.id.name} complete")
                    worker.move(Status.Complete)
                    worker.done()
                    notify(context, "Done")
                }
                is WorkResult.Next -> {
                    val (id, uuid) = ctx.ids.next()
                    ctx.channel.send(Command.WorkerCommand(id, uuid.toString(), Action.Process, worker.id, 0, ""))
                }
                is WorkResult.More -> {
                    val (id, uuid) = ctx.ids.next()
                    ctx.channel.send(Command.WorkerCommand(id, uuid.toString(), Action.Process, worker.id, 0, ""))
                }
                else -> {
                    ctx. logger.error("Worker in unexpected state ${worker.id.id}, ${workResult.name}")
                }
            }
            ""
        }
        result.onFailure {
            ctx.logger.error("Error while looping on : ${worker.id.id}")
        }
    }

    private fun record(id: Identity, action: String, extra: List<Pair<String, String>> = listOf()) {
        val pairs = listOf("id" to id.id, "action" to action) + extra
        ctx.logger.log(LogLevel.Info, "Workers:", pairs)
    }

    private suspend fun notify(context: WorkerContext, action: String) {
        try {
            val worker = context.worker
            val task = context.task
            record(worker.id, action, task.structured())
            ctx.notifier.notify(worker)
        } catch (ex: Exception) {
        }
    }
}
