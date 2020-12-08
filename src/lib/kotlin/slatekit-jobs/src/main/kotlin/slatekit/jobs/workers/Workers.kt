package slatekit.jobs.workers

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.Action
import slatekit.jobs.Event
import slatekit.jobs.Task
import slatekit.tracking.Recorder
import slatekit.jobs.support.*
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

/**
 * Represents a cluster of Workers that are affiliated with 1 job.
 * This helps manage the coordination between a @see[Worker] and a @see[slatekit.jobs.Job]
 * This is done by this class interpreting the @see[WorkResult] returned by a Worker
 * Based on the WorkResult, this may send commands @see[slatekit.jobs.support.Command]s to a Job's Channel
 * Essentially, this works like a glorified loop over each work, continuously:
 * 1. checking its WorkResult
 * 2. sending more commands to the Job's channel to pause, resume, process etc.
 * 3. handling errors
 * 4. notification of state changes
 */
class Workers(val ctx: JobContext) {
    private val PAUSE_IN_SECONDS = 30L
    private val events = ctx.notifier.wrkEvents
    private val lookup: Map<String, Executor> = ctx.workers.map { it.id.id to WorkerContext(it.id, it, Recorder.of(it.id), ctx.backoffs, ctx.policies) }
        .map { it.first to Executor.of(it.second) }.toMap()

    /**
     * Subscribe to status being changed for any worker
     */
    suspend fun on(op: suspend (Event) -> Unit) {
        events.on(op)
    }

    /**
     * Subscribe to status beging changed to the one supplied for any worker
     */
    suspend fun on(status: Status, op: suspend (Event) -> Unit) {
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
    fun getIds(): List<Identity> = ctx.workers.map { it.id }

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
                val cmd = ctx.commands.work(worker.id, Action.Resume)
                ctx.channel.send(cmd)
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
                val cmd = ctx.commands.work(worker.id, Action.Resume)
                ctx.channel.send(cmd)
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
            val cmd = ctx.commands.work(id, Action.Start)
            ctx.channel.send(cmd)
        }
    }

    private suspend fun perform(status: Status?, reason: String?, id: Identity, operation: suspend (Executor) -> Outcome<Status>): Outcome<Status> {
        record(id, status?.name ?: reason ?: "")
        return when (val executor = this.lookup[id.id]) {
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
                    notify(context, Status.Complete.name)
                }
                is WorkResult.Next -> {
                    val cmd = ctx.commands.work(worker.id, Action.Process)
                    ctx.channel.send(cmd)
                }
                is WorkResult.More -> {
                    val cmd = ctx.commands.work(worker.id, Action.Process)
                    ctx.channel.send(cmd)
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
        //ctx.logger.log(LogLevel.Info, "Workers:", pairs)
    }

    private suspend fun notify(context: WorkerContext, action: String) {
        try {
            val worker = context.worker
            val task = context.task
            record(worker.id, action, task.structured())
            ctx.notifier.notify(ctx, context)
        } catch (ex: Exception) {
        }
    }
}
