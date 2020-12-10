package slatekit.jobs

import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.support.Support
import slatekit.jobs.support.Utils
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
    suspend fun delay(wctx: WorkerContext, handle: Boolean = true, notify: Boolean = true, seconds: Long = 30): Try<Status> {
        return perform(wctx.id, Status.InActive, notify = notify, msg = "Delayed") {
            if (handle) {
                schedule(seconds, Action.Start, wctx.id)
            }
        }
    }


    /**
     * Start worker and moves it to running
     * Only requires init method on worker
     */
    suspend fun start(wctx: WorkerContext, handle: Boolean = true, notify: Boolean = true): Try<Status> {
        return perform(wctx.id, Status.Started, notify = notify) {
            wctx.worker.start()
        }
        .then { perform(wctx.id, Status.Running, notify = notify ) {
            send(Action.Process, wctx.id)
        }}
    }


    /**
     * Pauses the worker for X seconds.
     * Schedules a command to the channel afterwards to resume
     */
    suspend fun pause(wctx: WorkerContext, handle: Boolean = true, notify: Boolean = true, seconds: Long = 30, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Paused, notify = notify) {
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
        return perform(wctx.id, Status.Running, notify = notify) {
            wctx.worker.resume(reason)
            send(Action.Process, wctx.id)
        }
    }


    /**
     * Stops the worker.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun stop(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Stopped, notify = notify) {
            wctx.worker.stop(reason)
        }
    }


    /**
     * Kills the worker.
     * Restart not possible
     */
    suspend fun kill(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Killed, notify = notify) {
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
        val current = wctx.worker.status()
        if(current == Status.Killed) return Tries.errored("Worker is killed, can not start")

        // Change status if not already running and notify everyone
        if (current != Status.Running) {
            move(Status.Running, true, wctx.id, null)
        }
        val status = Tries.of {
            val result = wctx.worker.work(task)

            // Handle result
            when (result) {
                is WorkResult.More -> send(Action.Process, wctx.id)
                is WorkResult.Next -> send(Action.Process, wctx.id)
                is WorkResult.Done -> {
                    move(Status.Completed, true, wctx.id, null)
                    wctx.worker.done()
                    send(Action.Check)
                }
                else -> {
                }
            }
            val status = Utils.toStatus(result)
            status
        }
        return handle(status, wctx.id)
    }


    suspend fun perform(id: Identity, newStatus: Status, notify: Boolean, msg: String? = null,
                        op: (suspend () -> Unit)? = null): Try<Status> {
        val result = Tries.of {
            op?.invoke()
            move(newStatus, notify, id, msg)
            newStatus
        }
        handle(result, id)
        return result
    }
}
