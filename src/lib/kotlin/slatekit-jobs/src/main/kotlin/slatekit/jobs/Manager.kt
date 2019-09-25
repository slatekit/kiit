package slatekit.jobs

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries


class Manager(val worker: FreeWorker<*>, val scheduler: Scheduler) {

    private val pauseInSeconds = 30L


    /**
     * For communicating requests on the worker
     */
    private val channel = Channel<WorkAction>()


    suspend fun request(action: WorkAction) {
        when (action) {
            is WorkAction.NA -> {
            }
            is WorkAction.Start  -> channel.send(WorkAction.Start)
            is WorkAction.Pause  -> channel.send(WorkAction.Pause)
            is WorkAction.Stop   -> channel.send(WorkAction.Stop)
            is WorkAction.Resume -> channel.send(WorkAction.Resume)
            else                 -> error(action, "Unexpected action")
        }
    }


    private suspend fun receive(action: WorkAction) {
        when (action) {
            is WorkAction.NA      -> process()
            is WorkAction.Process -> process()
            is WorkAction.Start   -> start()
            is WorkAction.Pause   -> pause()
            is WorkAction.Stop    -> stop()
            is WorkAction.Resume  -> resume()
            else                  -> error(action)
        }
    }


    /**
     * Similar to resume.
     * Send message to actor to kick off the "process" flow
     */
    private suspend fun process() {
        when(worker){
            is FreeWorker<*> -> {
                val workState = worker.work()
                loop(worker, workState)
            }
            else -> {
                // TODO: Manage types better
                throw Exception("Unexpected type")
            }
        }
    }


    private suspend fun start() {
        info("Starting worker")
        val result: Try<WorkState> = Workers.start(worker)
        when(result){
            is Success -> {
                loop(worker, result.value)
            }
            is Failure -> {
                error(WorkAction.Start, "Unable to start job")
            }
        }

    }


    private suspend fun pause() {
        info("Pausing worker")
        WorkerUtils.handlePausable(worker) { result ->
            when(result){
                is Success -> {
                    val pausable = result.value
                    worker.transition(Status.Paused)
                    pausable.pause("Paused")
                    scheduler.schedule(DateTime.now().plusSeconds(pauseInSeconds)) { resume() }
                }
                is Failure -> {
                    error(WorkAction.Pause, result.msg)
                }

            }
        }
    }


    private suspend fun resume() {
        info("Resuming worker")
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


    private suspend fun stop() {
        info("Stopping worker")
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


    /**
     * Similar to resume.
     * Send message to actor to kick off the "process" flow
     */
    private suspend fun proceed() {
        channel.send(WorkAction.Process)
    }


    private suspend fun info(msg: String) {

    }


    private suspend fun error(action: WorkAction, msg:String? = null) {

    }


    /**
     * We keep looping to process the job by sending "JobActions.Process" message
     * to this actor ( a self sending actor ).
     * This is done to avoid concurrency / synchronization issues.
     *
     * @param jobState : Whether or not there is more data to process
     */
    suspend fun loop(worker: Workable<*>, state: WorkState) {
        val result = Tries.attempt {
            when (state) {
                is WorkState.Done -> {
                    info("Worker ${worker.id.name} complete")
                    worker.transition(Status.Complete)
                    worker.done()
                }
                is WorkState.More -> {
                    proceed()
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
}