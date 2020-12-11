package slatekit.jobs.support

import slatekit.actors.Status
import slatekit.common.Identity
import slatekit.jobs.workers.WorkResult
import slatekit.results.Codes

object Utils {

    fun isWorker(identity: Identity):Boolean {
        return identity.tags.contains("worker")
    }

    fun toStatus(result:WorkResult): Status {
        return when(result) {
            is WorkResult.Next  -> Status.Running
            is WorkResult.More  -> Status.Running
            is WorkResult.Stop  -> Status.Stopped
            is WorkResult.Done  -> Status.Completed
            is WorkResult.Fail  -> Status.Failed
            else                -> Status.Running
        }
    }

    fun toCode(status: Status): slatekit.results.Status {
        return when (status) {
            is Status.InActive -> Codes.INACTIVE
            is Status.Started -> Codes.STARTING
            is Status.Waiting     -> Codes.WAITING
            is Status.Running  -> Codes.RUNNING
            is Status.Paused   -> Codes.PAUSED
            is Status.Stopped  -> Codes.STOPPED
            is Status.Completed -> Codes.COMPLETE
            is Status.Failed   -> Codes.ERRORED
            else               -> Codes.SUCCESS
        }
    }
}
