package kiit.jobs

import kiit.actors.Status

/**
 * Represents the result of a worker/operation performing a single task.
 * This provides a way to communicate back whether this is completely done or can continue
 */
sealed class WResult(val name: String) {
    object More : WResult("More")
    object Done : WResult("Done")
    object Stop : WResult("Stop")
    object Fail : WResult("Fail")
    data class Next(val offset: Long, val processed: Long, val reference: String) : WResult("next")


    fun toStatus(): Status {
        return when(this) {
            is Next -> Status.Running
            is More -> Status.Running
            is Stop -> Status.Stopped
            is Done -> Status.Completed
            is Fail -> Status.Failed
            else                -> Status.Running
        }
    }


    companion object {
        fun next(offset: Long, processed: Long, reference: String): WResult {
            return Next(offset, processed, reference)
        }
    }
}
