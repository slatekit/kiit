package slatekit.jobs


sealed class JobAction(val name:String) {
    object NA      : JobAction( "NA"     )
    object Start   : JobAction( "Start"  )
    object Stop    : JobAction( "Stop"   )
    object Pause   : JobAction( "Pause"  )
    object Resume  : JobAction( "Resume" )
    object Control : JobAction( "Control")
    object Process : JobAction( "Process")
    object Delay   : JobAction( "Delay" )
}


