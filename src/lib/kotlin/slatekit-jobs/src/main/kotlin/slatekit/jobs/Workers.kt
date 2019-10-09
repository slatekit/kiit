package slatekit.jobs

import kotlinx.coroutines.channels.SendChannel
import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.workers.slatekit.jobs.WorkLoop

class Workers(val workers:List<Worker<*>>,
              val workLoop:WorkLoop,
              val logger:Logger,
              val scheduler: Scheduler,
              val pauseInSeconds:Long) {

    private val lookup = workers.map { it.id.name to it }.toMap()


    operator fun get(id:Identity):Worker<*>? = when(lookup.containsKey(id.name)) {
        true -> lookup[id.name]
        false -> null
    }


    suspend fun start(id:Identity)  {
        perform("Starting", id) { worker ->
            val result = WorkRunner.record(worker) {
                val result: Try<WorkState> = WorkRunner.attemptStart(worker, false)
                result.toOutcome()
            }.inner()

            when (result) {
                is Success -> {
                    val state = result.value
                    workLoop.loop(worker, state)
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
        perform("Processing", id) { worker ->
            val result = WorkRunner.record(worker) {
                val state = worker.work()
                state
            }
            result.map { state -> workLoop.loop(worker, state) }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun resume(id:Identity, reason:String?) {
        performPausableAction(Status.Running, id) { worker, pausable ->
            val result = WorkRunner.record(worker) {
                val state = pausable.resume(reason ?: "Resuming")
                state
            }
            result.map { state -> workLoop.loop(worker, state) }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun pause(id:Identity, reason:String?) {
        performPausableAction(Status.Paused, id) { worker, pausable ->
            pausable.pause(reason ?: "Paused")
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSeconds)) {
                workLoop.request(JobRequest.WorkRequest(JobAction.Resume, worker.id, 0, ""))
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
        logger.info("Delaying worker in $seconds second(s)")
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            workLoop.request(JobRequest.WorkRequest(JobAction.Start, id, 0,""))
        }
    }



    private suspend fun perform(action:String, id:Identity, operation: suspend (Worker<*>) -> Outcome<Status>):Outcome<Status> {
        logger.info("$action worker")
        val worker = this[id]
        return when(worker) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> operation(worker)
        }
    }


    private suspend fun performPausableAction(status: Status, id:Identity, operation: suspend (Worker<*>, Pausable) -> Outcome<Status>):Outcome<Status> {
        logger.info("Transitioning worker to: $status")
        val worker = this[id]
        return when(worker) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                when (worker) {
                    is Pausable -> {
                        worker.transition(status)
                        operation(worker, worker)
                    }
                    else -> Outcomes.errored("${worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action")
                }
            }
        }
    }
}