package slatekit.jobs.workers


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
