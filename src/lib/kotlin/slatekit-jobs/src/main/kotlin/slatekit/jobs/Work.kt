package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.jobs.support.JobUtils
import slatekit.jobs.workers.WorkResult
import slatekit.jobs.workers.WorkerContext
import slatekit.results.Try
import slatekit.results.builders.Tries
import slatekit.results.then

object Work {

    /**
     * This is a delayed start of the worker in X seconds.
     * Schedules a command to the channel afterwards to start
     */
    suspend fun delay(job: Job, wctx: WorkerContext, seconds: Long): Try<Status> {
        return perform(job, wctx, Status.InActive, notify = true, msg = "Delayed") {
            schedule(job, wctx, seconds, Action.Start)
        }
    }

    /**
     * Moves the worker to the new status
     * Notifies all listeners of the job/worker events
     */
    suspend fun move(job: Job, wctx: WorkerContext, status: Status, notify: Boolean, msg: String?) {
        wctx.worker.move(status)
        if (notify) {
            job.ctx.notifier.notify(job.ctx, wctx, msg ?: "State changed")
        }
    }


    /**
     * Schedules a timed work command to be run in the supplied seconds.
     */
    suspend fun schedule(job: Job, wctx: WorkerContext, seconds: Long, action: Action) {
        job.ctx.scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            val cmd = job.ctx.commands.work(wctx.id, action)
            job.ctx.channel.send(cmd)
        }
    }

    /**
     * Start worker and moves it to running
     * Only requires init method on worker
     */
    suspend fun start(job: Job, wctx: WorkerContext): Try<Status> {
        return perform(job, wctx, Status.Started, notify = true)
            .then { init(job, wctx) }
    }


    /**
     * Initialize worker.
     * Calls the init life-cycle method on the worker.
     */
    suspend fun init(job: Job, wctx: WorkerContext): Try<Status> {
        return perform(job, wctx, Status.Running, notify = true) {
            wctx.worker.init()
        }
    }


    /**
     * Pauses the worker for X seconds.
     * Schedules a command to the channel afterwards to resume
     */
    suspend fun pause(job: Job, wctx: WorkerContext, seconds: Long): Try<Status> {
        return perform(job, wctx, Status.Paused, notify = true) {
            wctx.worker.pause("")
            schedule(job, wctx, seconds, Action.Resume)
        }
    }


    /**
     * Stops the worker.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun stop(job: Job, wctx: WorkerContext): Try<Status> {
        return perform(job, wctx, Status.Stopped, notify = true) {
            wctx.worker.stop("")
        }
    }


    /**
     * Works the worker ( with optional task )
     * This is the main method to perform the actual work on the task
     */
    suspend fun work(job: Job, wctx: WorkerContext, task: Task): Try<Status> {
        // Change status if not already running and notify everyone
        if (wctx.worker.status() != Status.Running) {
            move(job, wctx, Status.Running, true, null)
        }
        val status = Tries.of {
            val result = wctx.worker.work(task)

            // Handle result
            when(result) {
                is WorkResult.More  -> job.send(wctx.id, Action.Process, "")
                is WorkResult.Next  -> job.send(wctx.id, Action.Process, "")
                is WorkResult.Delay -> schedule(job, wctx, result.seconds.toLong(), Action.Start)
                else -> {}
            }
            val status = JobUtils.toStatus(result)
            status
        }
        return handle(job, wctx, status)
    }


    suspend fun perform(job: Job, wctx: WorkerContext, newStatus: Status, notify: Boolean, msg: String? = null,
                        op: (suspend () -> Unit)? = null): Try<Status> {
        val result = Tries.of {
            op?.invoke()
            move(job, wctx, newStatus, notify, msg)
            newStatus
        }
        handle(job, wctx, result)

        return result
    }

    suspend fun handle(job:Job, wctx: WorkerContext, result:Try<Status>): Try<Status> {
        result.onFailure { ex ->
            wctx.worker.move(Status.Failed, ex.message)
            job.ctx.notifier.notify(job.ctx, wctx)
        }
        return result
    }
}
