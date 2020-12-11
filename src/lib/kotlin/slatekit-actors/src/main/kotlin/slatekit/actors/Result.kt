package slatekit.actors

/**
 * Future use
 */
sealed class Result(val name: String) {
    object More : Result("More")
    object Done : Result("Done")
    object Stop : Result("Stop")
    object Fail : Result("Fail")
    data class Next(val offset: Long, val processed: Long, val reference: String) : Result("next")
}
