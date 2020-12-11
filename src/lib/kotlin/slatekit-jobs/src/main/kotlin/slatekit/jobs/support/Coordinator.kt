package slatekit.jobs.support

import slatekit.actors.Status
import slatekit.actors.Action
import slatekit.jobs.Job
import slatekit.jobs.workers.Work
import slatekit.jobs.workers.WorkerContext
import slatekit.results.Try
import slatekit.results.builders.Tries
import slatekit.results.then


/**
 * Controls a Job by :
 * 1. Moving its @see[slatekit.common.Status] ( Running to Paused )
 * 2. Moving the status of all the applicable Workers in this job
 * 3. Scheduling resuming the job via a scheduled Resume command
 * 4. Notifying listeners of status changes
 * Compliments the Work component, but this is essentially to control job
 */
class Coordinator(override val job: Job, val work: Work = Work(job)) : Support {

    /**
     * This is a delayed start of the job in X seconds.
     * Schedules a command to the channel afterwards to start
     */
    suspend fun delay(seconds: Long): Try<Status> {
        return move(Action.Delay, Status.InActive, msg = "Delayed") {
            each { wctx ->
                // Life-cycle hook + same command single worker
                work.delay(wctx, handle = false, notify = false, seconds = 30)
            }
            // This delays the start of the entire job.
            schedule(seconds, Action.Start)
        }
    }


    /**
     * Starts the job
     */
    suspend fun start(): Try<Status> {
        return move(Action.Start, Status.Started).then {
            move(Action.Process, Status.Running) {
                each { wctx -> work.start(wctx, handle = false) }
                each { wctx -> send(Action.Process, wctx.id) }
            }
        }
    }


    /**
     * Pauses the job for X seconds.
     * Schedules a command to the channel afterwards to resume
     */
    suspend fun pause(seconds: Long, reason: String? = null): Try<Status> {
        return move(Action.Pause, Status.Paused) {
            each { wctx ->
                // Life-cycle hook + same command single worker
                work.pause(wctx, handle = false, seconds = seconds, reason = reason)
            }
            schedule(seconds, Action.Resume)
        }
    }


    /**
     * Resumes the job
     */
    suspend fun resume(reason: String? = null): Try<Status> {
        return move(Action.Resume, Status.Running) {
            each { wctx ->
                // Life-cycle hook + same command single worker
                work.resume(wctx, reason = reason)
            }
        }
    }


    /**
     * Stops the job.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun stop(reason: String? = null): Try<Status> {
        return move(Action.Stop, Status.Stopped) {
            each { wctx -> work.stop(wctx, reason = reason) }
        }
    }


    /**
     * Stops the job.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun done(): Try<Status> {
        return Tries.of {
            move(Status.Completed, true, null, null)
            Status.Completed
        }
    }


    /**
     * Kills the job.
     * No restart is possible
     */
    suspend fun kill(reason: String? = null): Try<Status> {
        return move(Action.Kill, Status.Killed) {
            each { wctx -> work.kill(wctx, reason = reason) }
        }
    }


    /**
     * Checks a job by notifying listeners of its status/info
     */
    suspend fun check(): Try<Status> {
        val isDone = job.ctx.workers.all { it.status() == Status.Completed }
        return when(isDone) {
            true -> this.done()
            else -> {
                val result = notify("check")
                each { wctx -> work.check(wctx, reason = null) }
                result
            }
        }
    }


    /**
     * Iterates over all workers
     */
    private suspend fun each(op: suspend (WorkerContext) -> Unit) {
        job.ctx.workers.forEach { worker ->
            val wctx = job.workers[worker.id]
            wctx?.let {
                op(it)
            }
        }
    }


    private suspend fun move(action: Action, newStatus: Status, notify: Boolean = true, msg: String? = null,
                             op: (suspend () -> Unit)? = null): Try<Status> {
        val result = Tries.of {
            val currentStatus = job.status()
            if (!validate(action, currentStatus)) {
                throw Exception("ERROR: Moving job - action=${action.name}, job=${job.id}, currentState=${currentStatus.name}")
            }
            op?.invoke()
            move(newStatus, notify, null, msg)
            newStatus
        }
        handle(result, null)
        return result
    }
}
