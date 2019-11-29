package slatekit.jobs

/**
 * Represents the state of a single iteration of work done by a worker
 *
 * NOTES:
 * 1. Note to be confused with @see[slatekit.common.Status] which represents status of the whole Worker
 * 2. More : indicates a worker can keep handling more work
 * 3. Next : indicates a worker can completed this page/iteration of work and can proceed to the next page/iteration
 * 4. Complete : indicates a worker is done and should be transitioned to @see[slatekit.common.Status.Complete].
 *           this is particularly useful in the case where a worker does NOT work from tasks from a
 *           a @see[slatekit.common.queues.Queue], but is self managed
 * 5. Each worker needs to return this result so that its next state can be determined
 */
sealed class WorkState(val name: String) {
    object Unknown : WorkState("Unknown")
    object Done : WorkState("Done")
    object More : WorkState("More")
    object Fail : WorkState("Fail")
    object Stop : WorkState("Stop")
    data class Delay(val seconds: Int) : WorkState("Delay")
    data class Next(val offset: Long, val processed: Long, val reference: String) : WorkState("next")

    fun parse(name: String): WorkState {
        return when (name) {
            Done.name -> Done
            More.name -> More
            else -> {
                val tokens = name.split(".")
                val first = tokens[0]
                when (first) {
                    "Next" -> Next(tokens[1].toLong(), tokens[2].toLong(), tokens[3])
                    else -> Unknown
                }
            }
        }
    }
}
