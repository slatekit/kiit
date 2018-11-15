package slatekit.core.scheduler.core

sealed class ErrorMode(val name:String, val value:Int, val desc:String) {
    object Strict   : ErrorMode("Strict"  , 0, "No errors allowed, an exception will fail the task")
    object Moderate : ErrorMode("Moderate", 1, "Errors allowed, but will trigger a backoff/pause")
    object Flexible : ErrorMode("Flexible", 2, "Errors are allowed, they are simply logged and tracked")
}