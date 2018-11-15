package slatekit.common.status

/**
 * Represents the different "states" of a life-cycle
 */
sealed class Status(val name:String, val value:Int) {
    object InActive : Status("InActive", 0)
    object Idle     : Status("Idle"    , 1)
    object Running  : Status("Running" , 2)
    object Paused   : Status("Paused"  , 3)
    object Stopped  : Status("Stopped" , 4)
    object Complete : Status("Complete", 5)
    object Failed   : Status("Failed"  , 6)
}