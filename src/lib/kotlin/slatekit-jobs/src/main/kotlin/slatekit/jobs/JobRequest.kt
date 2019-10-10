package slatekit.jobs

import slatekit.common.ids.Identity
import java.util.*

sealed class JobRequest {
    abstract val id:Long
    abstract val uuid:String
    abstract val action: JobAction

    data class TaskRequest(override val id:Long,
                           override val uuid:String,
                           override val action: JobAction) : JobRequest()

    data class WorkRequest(override val id:Long,
                           override val uuid:String,
                           override val action: JobAction,
                           val target: Identity,
                           val seconds:Long = 0,
                           val desc:String?) : JobRequest()


    companion object {

        /**
         * Builds a work request
         */
        fun work(action: JobAction, workerId: Identity, seconds: Long, desc:String?):WorkRequest {
            return JobRequest.WorkRequest(0L, UUID.randomUUID().toString(), action, workerId, seconds, desc)
        }
    }
}