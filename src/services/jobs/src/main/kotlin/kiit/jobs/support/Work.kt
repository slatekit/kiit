package kiit.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kiit.actors.*
import kiit.actors.Action
import kiit.actors.Status
import kiit.common.Identity
import kiit.common.NOTE
import kiit.jobs.*
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try
import kiit.results.builders.Tries
import kiit.results.then

/**
 * Controls a Worker by :
 * 1. Moving its @see[kiit.actors.Status] ( Running to Paused )
 * 2. Scheduling resuming via a scheduled Resume command
 * 3. Notifying listeners of status changes
 * 4. Handling the running of a workers work method and checking for completion
 */
class Work(val job: Manager) {

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
            wctx.worker.started()
        }
        .then { perform(wctx.id, Status.Running, notify = notify ) {
            request(wctx.id)
        }}
    }


    /**
     * Pauses the worker for X seconds.
     * Schedules a command to the channel afterwards to resume
     */
    suspend fun pause(wctx: WorkerContext, handle: Boolean = true, notify: Boolean = true, seconds: Long = 30, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Paused, notify = notify) {
            wctx.worker.paused(reason)
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
            wctx.worker.resumed(reason)
            request(wctx.id)
        }
    }


    /**
     * Stops the worker.
     * Only way to start again is to issue the start/resume command
     */
    suspend fun stop(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Stopped, notify = notify) {
            wctx.worker.stopped(reason)
        }
    }


    /**
     * Kills the worker.
     * Restart not possible
     */
    suspend fun kill(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return perform(wctx.id, Status.Killed, notify = notify) {
            wctx.worker.killed(reason)
        }
    }


    /**
     * Checks the worker
     */
    suspend fun check(wctx: WorkerContext, notify: Boolean = true, reason: String? = null): Try<Status> {
        return Tries.of {
            notify(reason, wctx.id)
            job.status()
        }
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
                is WResult.More -> request(wctx.id)
                is WResult.Next -> request(wctx.id)
                is WResult.Done -> {
                    move(Status.Completed, true, wctx.id, null)
                    wctx.worker.completed(null)
                    job.check()
                }
                else -> {
                }
            }
            val status = result.toStatus()
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


    fun validate(action: Action, currentStatus: Status): Boolean = when(action) {
        is Action.Start  -> Rules.canStart(currentStatus)
        is Action.Pause  -> Rules.canPause(currentStatus)
        is Action.Resume -> Rules.canResume(currentStatus)
        is Action.Stop   -> Rules.canStop (currentStatus)
        is Action.Kill   -> Rules.canKill(currentStatus)
        else             -> true
    }


    suspend fun handle(result:Try<Status>, id: Identity): Try<Status> {
        when(result){
            is Success -> {
                if(result.value == Status.Completed){
                    move(Status.Completed, true, id, null)
                }
            }
            is Failure -> {
                move(Status.Failed, true, id, result.error.message)
            }
        }
        return result
    }

    /**
     * Moves the worker to the new status
     * @param status : New Status to move the job or worker to
     * @param notify : Whether or not to send out notifications
     * @param id     : Identity of Job/Worker, controls if Job or Work command is built

     */
    suspend fun move(status: Status, notify: Boolean, id:Identity, msg: String?) {

        job.get(id)?.worker?.move(status, msg)
        if (notify) {
            notify(msg, id)
        }
    }

    /**
     * Schedules a timed command to be run in the supplied seconds.
     * @param seconds : Number of seconds in the future from now to schedule this at
     * @param action  : Action for the command such as Start, Pause, Resume, etc
     * @param id      : Identity of Job/Worker, controls if Job or Work command is used
     */
    suspend fun schedule(seconds: Long, action: Action, id: Identity? = null) {
        job.ctx.scheduler.schedule(seconds) {
            job.control(action, "", id?.name ?: Message.NONE)
        }
    }


    private suspend fun request(id:Identity) {
        NOTE.REFACTOR("jobs", "Check if this can always be launched, it just seems tough to test")
        when(job.jctx.mode) {
            Channel.UNLIMITED -> job.load(Workers.shortId(id))
            else -> job.jctx.scope.launch { job.load(Workers.shortId(id)) }
        }
    }


    private suspend fun notify(reason:String?, id:Identity) {

    }
}
