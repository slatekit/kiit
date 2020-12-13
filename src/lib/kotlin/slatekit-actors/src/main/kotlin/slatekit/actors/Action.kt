package slatekit.actors

/**
 * Represents the actions that control the state @see[Status] of a @see[Managed] actor
 * 1. started
 * 2. stopped
 * 3. paused
 * 4. resumed
 * 5. Processed ( 1 time )
 * 6. Delayed ( delayed start )
 */
sealed class Action(val name: String) {
    /* ktlint-disable */
    object Start    : Action( "Start"  )
    object Pause    : Action( "Pause"  )
    object Resume   : Action( "Resume" )
    object Delay    : Action( "Delay"  )
    object Stop     : Action( "Stop"   )
    object Kill     : Action( "Kill"   )
    object Check    : Action( "Check"  )
    object Process  : Action( "Process")
    /* ktlint-enable */

    fun toStatus(current: Status): Status {
        return when(this) {
            Delay -> Status.InActive
            Start -> Status.Started
            Pause -> Status.Paused
            Resume -> Status.Running
            Stop -> Status.Stopped
            Kill -> Status.Killed
            Check -> current
            Process -> current
        }
    }
}
