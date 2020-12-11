package slatekit.jobs.support

import slatekit.actors.Status
import slatekit.actors.WResult
import slatekit.common.Identity
import slatekit.results.Codes

object Utils {

    fun isWorker(identity: Identity):Boolean {
        return identity.tags.contains("worker")
    }

    fun toStatus(result:WResult): Status {
        return when(result) {
            is WResult.Next  -> Status.Running
            is WResult.More  -> Status.Running
            is WResult.Stop  -> Status.Stopped
            is WResult.Done  -> Status.Completed
            is WResult.Fail  -> Status.Failed
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
