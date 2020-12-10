package slatekit.actors

/**
 * Represents the different "states" of a life-cycle
 */
sealed class Status(val name:String, val value:Int) {
    object InActive  : Status("InActive" , 0)
    object Started   : Status("Started"  , 1)
    object Running   : Status("Running"  , 2)
    object Paused    : Status("Paused"   , 3)
    object Stopped   : Status("Stopped"  , 4)
    object Completed : Status("Completed", 5)
    object Failed    : Status("Failed"   , 6)
    object Killed    : Status("Killed"   , 7)
}
