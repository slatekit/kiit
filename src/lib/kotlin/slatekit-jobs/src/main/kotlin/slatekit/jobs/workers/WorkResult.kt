package slatekit.jobs.workers

/**
 * Used as a return value for a work function/worker.
 * 1. Done  -> Indicate to system that the work is done
 * 2. More  -> Indicate to system that the work is requesting more work ( from a queue )
 * 3. Stop  -> Indicate to system that the work needs to stop
 * 4. Delay -> Indicate to the system to delay work for x seconds
 * 5. Next  -> Indicate to the system of a transition to the next page / batch of work
 *
 * NOTES:
 * 1. The More and Next values allow a worker to "yield" for short time to allow other instances to run
 */
sealed class WorkResult(val name: String) {
    object Unknown : WorkResult("Unknown")
    object Done    : WorkResult("Done")
    object More    : WorkResult("More")
    object Fail    : WorkResult("Fail")
    object Stop    : WorkResult("Stop")

    data class Delay(val seconds: Int) : WorkResult("Delay")
    data class Next(val offset: Long, val processed: Long, val reference: String) : WorkResult("next")


    fun parse(name: String): WorkResult {
        return when (name) {
            Done.name -> Done
            More.name -> More
            else -> {
                val tokens = name.split(".")
                val first = tokens[0]
                when (first.toLowerCase()) {
                    "next"  -> Next(tokens[1].toLong(), tokens[2].toLong(), tokens[3])
                    "delay" -> Delay(tokens[1].toInt())
                    else -> Unknown
                }
            }
        }
    }


    companion object {
        fun next(offset: Long, processed: Long, reference: String): WorkResult {
            return WorkResult.Next(offset, processed, reference)
        }
    }
}
