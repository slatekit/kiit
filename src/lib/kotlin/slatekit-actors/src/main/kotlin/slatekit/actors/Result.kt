package slatekit.actors

sealed class Result(val name: String) {
    object Done : Result("Done")
    object More : Result("More")
    object Fail : Result("Fail")
    object Stop : Result("Stop")
    data class Next(val offset: Long, val processed: Long, val reference: String) : Result("next")
}
