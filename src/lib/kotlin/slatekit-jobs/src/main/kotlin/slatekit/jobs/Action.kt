package slatekit.jobs

/**
 * Represents the actions that can be performed on either a Job or worker.
 * NOTE: Either a Job or Worker can be
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
    object Check    : Action( "Check"  )
    object Process  : Action( "Process")
    /* ktlint-enable */
}

