package slatekit.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.log.Logger
import slatekit.results.Err
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries


class Manager(all: List<Worker<*>>, val scheduler: Scheduler, val logger: Logger) {

    private val workers = all.map { WorkContext(it, WorkerStats.of(it.id)) }
    private val pauseInSeconds = 30L


    private val channel = Channel<WorkAction>()


    suspend fun request(action: WorkAction) {
        when (action) {
            is WorkAction.NA     -> info("nothing yet")
            is WorkAction.Start  -> channel.send(WorkAction.Start)
            is WorkAction.Pause  -> channel.send(WorkAction.Pause)
            is WorkAction.Stop   -> channel.send(WorkAction.Stop)
            is WorkAction.Resume -> channel.send(WorkAction.Resume)
            else                 -> error(action, "Unexpected action")
        }
    }


    suspend fun requestStart() {
        channel.send(WorkAction.Start)
    }


    suspend fun requestProceed() {
        channel.send(WorkAction.Process)
    }


    suspend fun requestResume() {
        channel.send(WorkAction.Resume)
    }


    private suspend fun receive(action: WorkAction) {
        val worker = workers.first()
        when (action) {
            is WorkAction.NA      -> process(worker)
            is WorkAction.Process -> process(worker)
            is WorkAction.Start   -> start(worker)
            is WorkAction.Delay   -> delay(worker, action.seconds)
            is WorkAction.Pause   -> pause(worker)
            is WorkAction.Stop    -> stop(worker)
            is WorkAction.Resume  -> resume(worker)
            else                  -> error(action)
        }
    }


    private suspend fun process(context: WorkContext) {
        val worker = context.worker
        when(worker){
            is Worker<*> -> {
                track(worker) { w -> w.work() }
            }
            else -> {
                // TODO: Manage types better
                throw Exception("Unexpected type")
            }
        }
    }


    private suspend fun start(context: WorkContext) {
        info("Starting worker")
        val worker = context.worker
        val result: Try<WorkState> = Workers.start(worker as Worker<*>)
        when(result){
            is Success -> {
                loop(worker, result.value)
            }
            is Failure -> {
                error(WorkAction.Start, "Unable to start job")
            }
        }
    }


    private suspend fun delay(context: WorkContext, seconds:Long) {
        info("Starting worker in $seconds second(s)")
        scheduler.schedule(DateTime.now().plusSeconds(seconds)) { requestStart() }
    }


    private suspend fun pause(context: WorkContext) {
        info("Pausing worker")
        val worker = context.worker
        WorkerUtils.handlePausable(worker) { result ->
            when(result){
                is Success -> {
                    val pausable = result.value
                    worker.transition(Status.Paused)
                    pausable.pause("Paused")
                    scheduler.schedule(DateTime.now().plusSeconds(pauseInSeconds)) { requestResume() }
                }
                is Failure -> {
                    error(WorkAction.Pause, result.msg)
                }

            }
        }
    }


    private suspend fun resume(context: WorkContext) {
        info("Resuming worker")
        val worker = context.worker
        WorkerUtils.handlePausable(worker) { result ->
            when(result){
                is Success -> {
                    val pausable = result.value
                    worker.transition(Status.Running)
                    val workState = pausable.resume("Resuming")
                    loop(worker, workState)
                }
                is Failure -> {
                    error(WorkAction.Resume, result.msg)
                }
            }
        }
    }


    private suspend fun stop(context: WorkContext) {
        info("Stopping worker")
        val worker = context.worker
        WorkerUtils.handlePausable(worker) { result ->
            when(result){
                is Success -> {
                    val pausable = result.value
                    worker.transition(Status.Stopped)
                    pausable.stop("Stopped")
                }
                is Failure -> {
                    error(WorkAction.Resume, result.msg)
                }
            }
        }
    }


    private suspend fun info(msg: String) {
        logger.info(msg)
    }


    private suspend fun error(action: WorkAction, msg:String? = null) {
        logger.error(msg ?: action.name, null)
    }


    suspend fun loop(worker: Workable<*>, state: WorkState) {
        val result = Tries.attempt {
            when (state) {
                is WorkState.Done -> {
                    info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkState.More -> {
                    requestProceed()
                }
            }
            ""
        }
        when (result) {
            is Success -> {
                println("ok")
            }
            is Failure -> {
                error(WorkAction.Process, "Error while looping")
            }
        }
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
}