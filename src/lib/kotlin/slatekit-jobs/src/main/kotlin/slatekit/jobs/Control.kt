package slatekit.jobs

import slatekit.common.Status
import slatekit.jobs.workers.WorkerContext
import slatekit.results.Try
import slatekit.results.builders.Tries


/**
 * Controls a Job by :
 * 1. Moving its @see[slatekit.common.Status] ( Running to Paused )
 * 2. Moving the status of all the applicable Workers in this job
 * 3. Scheduling resuming the job via a scheduled Resume command
 * 4. Notifying listeners of status changes
 * Compliments the Work component, but this is essentially to control job
 */
class Control(override val job:Job, val work:Work = Work(job)) : Support {

    /**
     * This is a delayed start of the job in X seconds.
     * Schedules a command to the channel afterwards to start
     */
    suspend fun delay(seconds: Long): Try<Status> {
        return move(Action.Delay, Status.InActive, notify = true, msg = "Delayed") {
            // This delays the start of the entire job.
            schedule(seconds, Action.Start)
        }
    }


    /**
     * Starts the job
     */
    suspend fun start(): Try<Status> {
        return move(Action.Start, Status.Started, notify = true) {
            each { wctx ->
                // Life-cycle hook + same command single worker
                work.start(wctx)
            }
        }
    }


    /**
     * Pauses the job for X seconds.
     * Schedules a command to the channel afterwards to resume
     */
    suspend fun pause(seconds: Long, reason:String? = null): Try<Status> {
        return move(Action.Pause, Status.Paused, notify = true) {
            each { wctx ->
                // Life-cycle hook + same command single worker
                work.pause(wctx, seconds, reason)
            }
            schedule(seconds, Action.Resume)
        }
    }


    /**
     * Resumes the job
     */
    suspend fun resume(reason:String ? = null): Try<Status> {
        return move(Action.Resume, Status.Running, notify = true) {
            each { wctx ->
                // Life-cycle hook + same command single worker
                work.resume(wctx, reason)
            }
        }
    }


    /**
     * Stops the job.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun stop(reason:String ? = null): Try<Status> {
        return move(Action.Stop, Status.Stopped, notify = true) {
            each { wctx -> work.stop(wctx, reason) }
        }
    }


    /**
     * Kills the job.
     * No restart is possible
     */
    suspend fun kill(reason:String ? = null): Try<Status> {
        return move(Action.Kill, Status.Killed, notify = true) {
            each { wctx -> wctx.worker.kill(reason) }
        }
    }


    /**
     * Checks a job by notifying listeners of its status/info
     */
    suspend fun check(): Try<Status> {
        val result = notify("check")
        each { wctx -> work.check(wctx,"") }
        return result
    }


    /**
     * Iterates over all workers
     */
    private suspend fun each(op:suspend(WorkerContext) -> Unit) {
        job.ctx.workers.forEach { worker ->
            val wctx = job.workers[worker.id]
            wctx?.let {
                op(it)
            }
        }
    }


    private suspend fun move(action: Action, newStatus: Status, notify: Boolean, msg: String? = null,
                             op: (suspend () -> Unit)? = null): Try<Status> {
        val result = Tries.of {
            val currentStatus = job.status()
            if(!validate(action, currentStatus)) {
                throw Exception("ERROR: Moving job - action=${action.name}, job=${job.id}, currentState=${currentStatus.name}")
            }
            op?.invoke()
            move(newStatus, notify, job.id, msg)
            newStatus
        }
        handle(result, null)
        return result
    }

    private fun move(condition:Boolean, status:Status, newStatus: Status){

        val isRunning = status == Status.Running

        // No need to change to same status
        if(status != newStatus) {
            when(newStatus) {
                Status.InActive  -> {}
                Status.Started   -> {}
                Status.Waiting   -> {}
                Status.Running   -> {}
                Status.Paused    -> {}
                Status.Stopped   -> {}
                Status.Completed -> {}
                Status.Failed    -> {}
            }
        }
    }
}
