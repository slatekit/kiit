package slatekit.common.status

/**
 * Represents the different "states" of a life-cycle
 */
sealed class Status(val name:String, val value:Int) {
    object InActive : Status("InActive", 0)
    object Starting : Status("Starting", 1)
    object Idle     : Status("Idle"    , 2)
    object Running  : Status("Running" , 3)
    object Paused   : Status("Paused"  , 4)
    object Stopped  : Status("Stopped" , 5)
    object Complete : Status("Complete", 6)
    object Failed   : Status("Failed"  , 7)
}