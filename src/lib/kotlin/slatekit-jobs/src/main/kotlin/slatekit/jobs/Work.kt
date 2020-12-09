package slatekit.jobs

import slatekit.common.Status
import slatekit.jobs.support.JobUtils
import slatekit.jobs.workers.WorkResult
import slatekit.jobs.workers.WorkerContext
import slatekit.results.Try
import slatekit.results.builders.Tries
import slatekit.results.then

class Work(override val job: Job) : Support {

    /**
     * This is a delayed start of the worker in X seconds.
     * Schedules a command to the channel afterwards to start
     */
    suspend fun delay(wctx: WorkerContext, handle:Boolean = true, notify: Boolean = true, seconds: Long = 30): Try<Status> {
        return perform(wctx.id, Status.InActive, notify = notify, msg = "Delayed") {
            if(handle){
                schedule(seconds, Action.Start, wctx.id)
            }
        }
    }


    /**
     * Start worker and moves it to running
     * Only requires init method on worker
     */
    suspend fun start(wctx: WorkerContext, notify: Boolean = true): Try<Status> {
        return perform(wctx.id, Status.Started, notify = true)
            .then {
                perform(wctx.id, Status.Running, notify = true) { wctx.worker.start(); Status.Running }
            }
            .then {
                Tries.of { send(Action.Process, wctx.id); Status.Running }
            }
    }


    /**
     * Pauses the worker for X seconds.
     * Schedules a command to the channel afterwards to resume
     */
    suspend fun pause(wctx: WorkerContext, handle: Boolean = true, notify: Boolean = true, seconds: Long = 30, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Paused, notify = true) {
            wctx.worker.pause(reason)
            if (handle) {
                schedule(seconds, Action.Resume, wctx.id)
            }
        }
    }


    /**
     * Resumes the worker.
     * Life-cycle hook is called and command is sent to channe to continue processing
     */
    suspend fun resume(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Running, notify = true) {
            wctx.worker.resume(reason)
            send(Action.Process, wctx.id)
        }
    }


    /**
     * Stops the worker.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun stop(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Stopped, notify = true) {
            wctx.worker.stop(reason)
        }
    }


    /**
     * Kills the worker.
     * Restart not possible
     */
    suspend fun kill(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Killed, notify = true) {
            wctx.worker.kill(reason)
        }
    }


    /**
     * Checks the worker
     */
    suspend fun check(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return notify(reason, wctx.id)
    }


    /**
     * Works the worker ( with optional task )
     * This is the main method to perform the actual work on the task
     */
    suspend fun work(wctx: WorkerContext, task: Task): Try<Status> {
        // Change status if not already running and notify everyone
        if (wctx.worker.status() != Status.Running) {
            move(Status.Running, true, wctx.id, null)
        }
        val status = Tries.of {
            val result = wctx.worker.work(task)

            // Handle result
            when (result) {
                is WorkResult.More -> send(Action.Process, wctx.id)
                is WorkResult.Next -> send(Action.Process, wctx.id)
                is WorkResult.Delay -> schedule(result.seconds.toLong(), Action.Start, wctx.id)
                else -> {
                }
            }
            val status = JobUtils.toStatus(result)
            status
        }
        return handle(status, wctx.id)
    }
}
