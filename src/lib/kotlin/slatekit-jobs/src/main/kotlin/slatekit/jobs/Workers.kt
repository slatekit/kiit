package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Info
import slatekit.common.log.Logger
import slatekit.common.metrics.Calls
import slatekit.results.*
import slatekit.results.builders.Outcomes

class Workers(val all:List<Worker<*>>,
              val coordinator:Coordinator,
              val scheduler: Scheduler,
              val logger:Logger,
              val ids:JobId,
              val pauseInSeconds:Long) {

    private val lookup = all.map { it.id.name to WorkerContext(it, Calls(it.id)) }.toMap()


    operator fun get(id:Identity):WorkerContext? = when(lookup.containsKey(id.name)) {
        true -> lookup[id.name]
        false -> null
    }


    suspend fun start(id:Identity)  {
        perform("Starting", id) { context ->
            val worker = context.worker
            val result = WorkRunner.record(context) {
                val result: Try<WorkState> = WorkRunner.attemptStart(worker, false)
                result.toOutcome()
            }.inner()

            when (result) {
                is Success -> {
                    val state = result.value
                    coordinator.loop(worker, state)
                    worker.status()
                }
                is Failure -> {
                    logger.error("Unable to start worker ${id.fullName}")
                    Status.Failed
                }
            }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun process(id:Identity) {
        perform("Processing", id) { context ->
            val worker = context.worker
            val result = WorkRunner.record(context) {
                val state = worker.work()
                state
            }
            result.map { state -> coordinator.loop(worker, state) }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun resume(id:Identity, reason:String?) {
        performPausableAction(Status.Running, id) { context, pausable ->
            val worker = context.worker
            val result = WorkRunner.record(context) {
                val state = pausable.resume(reason ?: "Resuming")
                state
            }
            result.map { state -> coordinator.loop(worker, state) }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun pause(id:Identity, reason:String?) {
        performPausableAction(Status.Paused, id) { context, pausable ->
            val worker = context.worker
            pausable.pause(reason ?: "Paused")
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSeconds)) {
                coordinator.request(JobRequest.WorkRequest(ids.nextId(), ids.nextUUID().toString(), JobAction.Resume, worker.id, 0, ""))
            }
            Outcomes.success(Status.Paused)
        }
    }


    suspend fun stop(id:Identity, reason:String?) {
        performPausableAction(Status.Stopped, id) { worker, pausable ->
            pausable.stop(reason ?: "Stopped")
            Outcomes.success(Status.Stopped)
        }
    }


    suspend fun delay(id:Identity, seconds:Long) {
        logger.log(Info, "Worker:", listOf("id" to id.name, "action" to "delaying", "seconds" to "$seconds"))
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            coordinator.request(JobRequest.WorkRequest(ids.nextId(), ids.nextUUID().toString(), JobAction.Start, id, 0,""))
        }
    }



    private suspend fun perform(action:String, id:Identity, operation: suspend (WorkerContext) -> Outcome<Status>):Outcome<Status> {
        logger.log(Info, "Worker:", listOf("id" to id.name, "action" to action))
        val worker = this[id]
        return when(worker) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> operation(worker)
        }
    }


    private suspend fun performPausableAction(status: Status, id:Identity, operation: suspend (WorkerContext, Pausable) -> Outcome<Status>):Outcome<Status> {
        logger.log(Info, "Worker:", listOf("id" to id.name, "transition" to status.name))
        val context = this[id]
        return when(context) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                when (context.worker) {
                    is Pausable -> {
                        context.worker.transition(status)
                        operation(context, context.worker)
                    }
                    else -> Outcomes.errored("${context.worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action")
                }
            }
        }
    }
}