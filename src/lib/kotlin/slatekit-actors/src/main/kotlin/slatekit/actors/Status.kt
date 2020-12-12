package slatekit.actors

import slatekit.results.Codes

/**
 * Represents the different "states" a @see[Managed] actor can be in
 */
sealed class Status(val name:String, val value:Int) {
    object InActive  : Status("InActive" , 0)
    object Started   : Status("Started"  , 1)
    object Waiting   : Status("Waiting"  , 2)
    object Running   : Status("Running"  , 3)
    object Paused    : Status("Paused"   , 4)
    object Stopped   : Status("Stopped"  , 5)
    object Completed : Status("Completed", 6)
    object Failed    : Status("Failed"   , 7)
    object Killed    : Status("Killed"   , 8)

    fun toCode(): slatekit.results.Status {
        return when (this) {
            is InActive  -> Codes.INACTIVE
            is Started   -> Codes.STARTING
            is Waiting   -> Codes.WAITING
            is Running   -> Codes.RUNNING
            is Paused    -> Codes.PAUSED
            is Stopped   -> Codes.STOPPED
            is Completed -> Codes.COMPLETE
            is Failed    -> Codes.ERRORED
            else         -> Codes.SUCCESS
        }
    }
}
