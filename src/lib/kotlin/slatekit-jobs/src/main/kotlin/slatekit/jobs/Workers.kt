package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.Identity
import slatekit.common.log.Info
import slatekit.common.log.Logger
import slatekit.common.metrics.Recorder
import slatekit.functions.policy.Policy
import slatekit.jobs.events.Events
import slatekit.jobs.events.WorkerEvents
import slatekit.jobs.support.*
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

/**
 * Represents a cluster of Workers that are affiliated with 1 job.
 */
class Workers(val jobId:Identity,
              val all:List<Worker<*>>,
              val coordinator: Coordinator,
              val scheduler: Scheduler,
              val logger:Logger,
              val ids: JobId,
              val pauseInSeconds:Long) : Events<Worker<*>> {

    private val events: Events<Worker<*>> = WorkerEvents(this)
    private val lookup = all.map { it.id.id to WorkerContext(jobId, it, Recorder.of(it.id)) }.toMap()


    /**
     * adds a policy to the job
     */
    fun policy(policy: Policy<WorkRequest, WorkResult>) {

    }


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
    operator fun get(id: Identity):WorkerContext? = when(lookup.containsKey(id.id)) {
        true -> lookup[id.id]
        false -> null
    }


    /**
     * Gets the WorkContext for the worker with the supplied identity.
     * This is to allow for looking up the job/stats metadata for a worker.
     */
    operator fun get(id:String):WorkerContext? = when(lookup.containsKey(id)) {
        true -> lookup[id]
        false -> null
    }

    /**
     * Gets all the worker ids
     */
    fun getIds():List<String> = all.map { it.id.id }


    /**
     * Starts the worker associated with the identity and makes it work using the supplied Task
     */
    suspend fun start(id: Identity, task: Task = Task.empty)  {
        perform("Starting", id) { context ->
            val worker = context.worker
            val result = WorkRunner.record(context) {
                val result: Try<WorkResult> = WorkRunner.attemptStart(worker, false, true, task)
                result.toOutcome()
            }.inner()

            when (result) {
                is Success -> {
                    val res = result.value
                    loop(worker, res)
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
        perform("Processing", id) { context ->
            val worker = context.worker
            val result = WorkRunner.record(context) {
                worker.work(task)
            }
            result.map { res -> loop(worker, res) }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun resume(id: Identity, reason:String?, task: Task = Task.empty) {
        performPausableAction(Status.Running, id) { context, pausable ->
            val worker = context.worker
            val result = WorkRunner.record(context) {
                pausable.resume(reason ?: "Resuming", task)
            }
            result.map { res -> loop(worker, res) }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun pause(id: Identity, reason:String?) {
        performPausableAction(Status.Paused, id) { context, pausable ->
            val worker = context.worker
            pausable.pause(reason ?: "Paused")
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSeconds)) {
                coordinator.request(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), JobAction.Resume, worker.id, 0, ""))
            }
            Outcomes.success(Status.Paused)
        }
    }


    suspend fun stop(id: Identity, reason:String?) {
        performPausableAction(Status.Stopped, id) { worker, pausable ->
            pausable.stop(reason ?: "Stopped")
            Outcomes.success(Status.Stopped)
        }
    }


    suspend fun delay(id: Identity, seconds:Long) {
        logger.log(Info, "Worker:", listOf("id" to id.name, "action" to "delaying", "seconds" to "$seconds"))
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            coordinator.request(Command.WorkerCommand(ids.nextId(), ids.nextUUID().toString(), JobAction.Start, id, 0,""))
        }
    }



    private suspend fun perform(action:String, id: Identity, operation: suspend (WorkerContext) -> Outcome<Status>):Outcome<Status> {
        logger.log(Info, "Worker:", listOf("id" to id.name, "action" to action))
        val context = this[id]
        return when(context) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                val result = operation(context)
                result
            }
        }
    }


    private suspend fun performPausableAction(status: Status, id: Identity, operation: suspend (WorkerContext, Pausable) -> Outcome<Status>):Outcome<Status> {
        logger.log(Info, "Worker:", listOf("id" to id.name, "transition" to status.name))
        val context = this[id]
        return when(context) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                when (context.worker) {
                    is Pausable -> {
                        context.worker.transition(status)
                        GlobalScope.launch {
                            (events as WorkerEvents).notify(context.worker)
                        }
                        operation(context, context.worker)
                    }
                    else -> Outcomes.errored("${context.worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action")
                }
            }
        }
    }


    private suspend fun loop(worker: Workable<*>, workResult: WorkResult) {
        val result = Tries.attempt {
            when (workResult.state) {
                is WorkState.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkState.Next -> {
                    val (id, uuid) = ids.next()
                    coordinator.request(Command.WorkerCommand(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
                }
                is WorkState.More -> {
                    val (id, uuid) = ids.next()
                    coordinator.request(Command.WorkerCommand(id, uuid.toString(), JobAction.Process, worker.id, 0, ""))
                }
            }
            ""
        }
        when (result) {
            is Success -> {  }
            is Failure -> {
                logger.error("Error while looping on : ${worker.id.id}")
            }
        }
    }
}