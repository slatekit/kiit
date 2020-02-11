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
    object Stop     : Action( "Stop"   )
    object Pause    : Action( "Pause"  )
    object Resume   : Action( "Resume" )
    object Control  : Action( "Control")
    object Process  : Action( "Process")
    object Delay    : Action( "Delay" )
    /* ktlint-enable */
}
