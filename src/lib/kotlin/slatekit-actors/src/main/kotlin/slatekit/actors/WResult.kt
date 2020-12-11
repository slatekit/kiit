package slatekit.actors

/**
 * Future use
 */
sealed class WResult(val name: String) {
    object More : WResult("More")
    object Done : WResult("Done")
    object Stop : WResult("Stop")
    object Fail : WResult("Fail")
    data class Next(val offset: Long, val processed: Long, val reference: String) : WResult("next")


    companion object {
        fun next(offset: Long, processed: Long, reference: String): WResult {
            return WResult.Next(offset, processed, reference)
        }
    }
}
