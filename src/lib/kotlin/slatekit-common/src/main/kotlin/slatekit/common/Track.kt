package slatekit.common

/**
 * Represents the different "states" of a life-cycle
 */
sealed class Track(val name:String, val value:Int) {
    object Log      : Track("log"  , 1)
    object Alert    : Track("alert", 2)

    companion object {

        val all = listOf(Log, Alert)
    }
}