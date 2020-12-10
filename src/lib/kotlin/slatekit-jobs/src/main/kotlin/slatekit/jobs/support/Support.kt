package slatekit.jobs.support

import kotlinx.coroutines.launch
import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.Action
import slatekit.jobs.Job
import slatekit.jobs.support.Rules
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Support {
    val job: Job


    /**
     * Moves the worker to the new status
     * @param status : New Status to move the job or worker to
     * @param notify : Whether or not to send out notifications
     * @param id     : Identity of Job/Worker, controls if Job or Work command is built

     */
    suspend fun move(status: Status, notify: Boolean, id:Identity?, msg: String?) {
        when(id) {
            null -> job.move(status)
            else -> job.get(id)?.worker?.move(status, msg)
        }
        if (notify) {
            notify(msg, id)
        }
    }


    /**
     * Notifies listeners of the job about its state
     * @param action : Action for the command such as Start, Pause, Resume, etc
     * @param id     : Identity of Job/Worker, controls if Job or Work command is built
     */
    fun command(action: Action, id: Identity?): Command {
        return when (id) {
            null   -> job.ctx.commands.job(job.id, action)
            else   -> job.ctx.commands.work(id, action)
        }
    }


    /**
     * Resumes the job.
     * Life-cycle hook is called and command is sent to channe to continue processing
     * @param id      : Identity of Job/Worker, controls if Job or Work command is used
     */
    suspend fun send(action: Action, id: Identity? = null) {
        val cmd = command(action, id)
        job.ctx.channel.send(cmd)
    }


    /**
     * Schedules a timed command to be run in the supplied seconds.
     * @param seconds : Number of seconds in the future from now to schedule this at
     * @param action  : Action for the command such as Start, Pause, Resume, etc
     * @param id      : Identity of Job/Worker, controls if Job or Work command is used
     */
    suspend fun schedule(seconds: Long, action: Action, id: Identity? = null) {
        job.ctx.scheduler.schedule(DateTime.now().plusSeconds(seconds)) {
            send(action, id)
        }
    }


    /**
     * Notifies listeners of the job about its state
     * @param msg : Optional message to use in the notification
     * @param id  : Identity of Job/Worker, controls if job or work event is sent
     */
    suspend fun notify(msg: String?, id: Identity? = null): Try<Status> {
        return Tries.of {
            when (id) {
                null -> {
                    job.ctx.scope.launch {
                        job.ctx.notifier.notify(job, msg ?: "")
                    }
                    job.status()
                }
                else -> {
                    val worker = job.ctx.workers.first { it.id == id }
                    worker?.let { job.ctx.notifier.notify(job, worker, msg ?: "") }
                    worker.status()
                }
            }
        }
    }


    fun validate(action:Action, currentStatus:Status): Boolean = when(action) {
        is Action.Start  -> Rules.canStart(currentStatus)
        is Action.Pause  -> Rules.canPause(currentStatus)
        is Action.Resume -> Rules.canResume(currentStatus)
        is Action.Stop   -> Rules.canStop (currentStatus)
        is Action.Kill   -> Rules.canKill(currentStatus)
        else             -> true
    }


    suspend fun handle(result:Try<Status>, id: Identity?): Try<Status> {
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
}
