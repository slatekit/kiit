package slatekit.jobs

import slatekit.common.ids.Identity

sealed class JobRequest {
    abstract val action: JobAction

    data class TaskRequest(override val action: JobAction) : JobRequest()

    data class WorkRequest(override val action: JobAction,
                           val target: Identity,
                           val seconds:Long = 0,
                           val desc:String?) : JobRequest()
}