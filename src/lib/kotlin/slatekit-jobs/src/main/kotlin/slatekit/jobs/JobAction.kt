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
sealed class JobAction(val name:String) {
    object Start    : JobAction( "Start"  )
    object Stop     : JobAction( "Stop"   )
    object Pause    : JobAction( "Pause"  )
    object Resume   : JobAction( "Resume" )
    object Control  : JobAction( "Control")
    object Process  : JobAction( "Process")
    object Slow     : JobAction( "Slow"   )
    object Fast     : JobAction( "Fast"   )
    object Delay    : JobAction( "Delay" )
}