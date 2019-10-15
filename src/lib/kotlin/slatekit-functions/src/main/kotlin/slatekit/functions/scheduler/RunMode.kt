package slatekit.functions.scheduler

sealed class RunMode(val name:String, val value:Int, val desc:String) {
    object  Periodic : RunMode("Periodic", 0, "Runs periodically")
    object  OnDemand : RunMode("OnDemand", 1, "Runs manually on demand")
    object  Hybrid   : RunMode("Hybrid"  , 2, "Runs periodically and manually on demand")
}