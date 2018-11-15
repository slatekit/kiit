package slatekit.core.scheduler.core

/**
 * The Scheduled tasks can be :
 * 1. started
 * 2. paused
 * 3. resumed
 * 4. stopped
 * 5. completed ( if run mode is limited to x runs )
 * 6. failed ( if an exception occurred )
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