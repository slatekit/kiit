package slatekit.jobs

import kotlinx.coroutines.channels.SendChannel
import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

class Workers(val channel:SendChannel<JobRequest>,
              val workers:List<Worker<*>>,
              val logger:Logger,
              val scheduler: Scheduler,
              val pauseInSeconds:Long) {

    private val lookup = workers.map { it.id.name to it }.toMap()


    operator fun get(id:Identity):Worker<*>? = when(lookup.containsKey(id.name)) {
        true -> lookup[id.name]
        false -> null
    }


    suspend fun track(worker: Worker<*>, operation: suspend (Worker<*>) -> WorkState){
        worker.stats.lastRunTime.set(DateTime.now())
        worker.stats.totalRuns.incrementAndGet()
        try {
            val workState = operation(worker)
            worker.stats.totalRunsPassed.incrementAndGet()
            loop(worker, workState)
        } catch (ex:Exception){
            worker.stats.totalRunsFailed.incrementAndGet()
            worker.stats.lasts.unexpected(Task.empty, Err.of(ex))
        }
    }


    suspend fun start(id:Identity) : Outcome<Status> {
        return perform("Starting", id) { worker ->
            val result: Try<WorkState> = WorkRunner.start(worker)
            when (result) {
                is Success -> {
                    loop(worker, result.value)
                    Outcomes.success(worker.status())
                }
                is Failure -> {
                    logger.error("Unable to start worker ${id.fullName}")
                    Outcomes.errored(result.msg)
                }
            }
        }
    }


    suspend fun process(id:Identity) {
        perform("Starting", id) { worker ->
            track(worker) { w -> w.work() }
            Outcomes.success(Status.Running)
        }
    }


    suspend fun delay(id:Identity, seconds:Long) {
        logger.info("Starting worker in $seconds second(s)")
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            request(JobRequest.WorkRequest(JobAction.Start, id))
        }
    }


    suspend fun pause(id:Identity) {
        performPausableAction("Pausing", id) { worker, pausable ->
            worker.transition(Status.Paused)
            pausable.pause("Paused")
            scheduler.schedule(DateTime.now().plusSeconds(pauseInSeconds)) {
                request(JobRequest.WorkRequest(JobAction.Resume, worker.id))
            }
            Outcomes.success(Status.Paused)
        }
    }


    suspend fun resume(id:Identity) {
        performPausableAction("Resuming", id) { worker, pausable ->
            worker.transition(Status.Running)
            val workState = pausable.resume("Resuming")
            loop(worker, workState)
            Outcomes.success(Status.Running)
        }
    }


    suspend fun stop(id:Identity) {
        performPausableAction("Stopping", id) { worker, pausable ->
            worker.transition(Status.Stopped)
            pausable.stop("Stopped")
            Outcomes.success(Status.Stopped)
        }
    }


    private suspend fun loop(worker: Workable<*>, state: WorkState) {
        val result = Tries.attempt {
            when (state) {
                is WorkState.Done -> {
                    logger.info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkState.More -> {
                    request(JobRequest.WorkRequest(JobAction.Process, worker.id))
                }
            }
            ""
        }
        when (result) {
            is Success -> {
                println("ok")
            }
            is Failure -> {
                logger.error("Error while looping on : ${worker.id.fullName}")
            }
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


    private suspend fun performPausableAction(action:String, id:Identity, operation: suspend (Worker<*>, Pausable) -> Outcome<Status>):Outcome<Status> {
        logger.info("$action worker")
        val worker = this[id]
        return when(worker) {
            null -> Outcomes.errored("Unable to find worker with id : ${id.name}")
            else -> {
                when (worker) {
                    is Pausable -> operation(worker, worker)
                    else -> Outcomes.errored("${worker.id.name} does not implement Pausable and can not handle a pause/stop/resume action")
                }
            }
        }
    }


    private suspend fun request(request: JobRequest) {
        channel.send(request)
    }
}