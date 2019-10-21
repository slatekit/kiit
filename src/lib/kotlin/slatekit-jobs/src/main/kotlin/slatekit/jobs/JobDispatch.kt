package slatekit.jobs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import slatekit.common.Status
import slatekit.jobs.events.JobEvents
import slatekit.jobs.support.Command
import slatekit.jobs.support.JobUtils


/**
 * Dispatches requests
 */
class JobDispatch(val job:Job, val workers: Workers, val events:JobEvents) {

    suspend fun start(launch:Boolean)                 = transition(JobAction.Start  , Status.Running, launch)
    suspend fun stop(launch:Boolean)                  = transition(JobAction.Stop   , Status.Stopped, launch)
    suspend fun resume(launch:Boolean)                = transition(JobAction.Resume , Status.Running, launch)
    suspend fun process(launch:Boolean)               = transition(JobAction.Process, Status.Running, launch)
    suspend fun pause(launch:Boolean, seconds:Long)   = transition(JobAction.Pause  , Status.Paused, launch, seconds)
    suspend fun delayed(launch:Boolean, seconds:Long) = transition(JobAction.Start  , Status.Paused, launch, seconds)


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun transition(action: JobAction, newStatus: Status, launch:Boolean, seconds:Long = 0) {
        JobUtils.perform(job, action, job.status(), launch) {
            //logger.log(Info, "Job:", listOf(nameKey, "transition" to newStatus.name))
            job.setStatus(newStatus)

            GlobalScope.launch {
                events.notify(job)
            }

            workers.all.forEach {
                val (id, uuid) = job.nextIds()
                val req = Command.WorkerCommand(id, uuid.toString(), action, it.id, seconds, "")
                //logger.log(Info, "Job:", listOf(nameKey, "target" to req.target.id, "action" to req.action.name))
                job.request(req)
            }
        }
    }
}
