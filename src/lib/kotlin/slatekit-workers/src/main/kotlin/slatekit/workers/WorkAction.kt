package slatekit.workers.slatekit.workers

sealed class WorkAction(val name:String) {
    object NA      : WorkAction( "NA"     )
    object Start   : WorkAction( "Start"  )
    object Stop    : WorkAction( "Stop"   )
    object Pause   : WorkAction( "Pause"  )
    object Resume  : WorkAction( "Resume" )
    object Control : WorkAction( "Control")
    object Process : WorkAction( "Process")


    fun parse(name:String):WorkAction {
        return when(name) {
            Start.name   -> Start
            Stop.name    -> Stop
            Pause.name   -> Pause
            Resume.name  -> Resume
            Control.name -> Control
            Process.name -> Resume
            else         -> NA
        }
    }
}