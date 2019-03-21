package slatekit.workers

sealed class Event {
    data class JobEvent    (val sender:String, val name:String, val state:String, val job:Job) : Event()
    data class QueueEvent  (val sender:String, val name:String, val state:String, val queue: Queue?) : Event()
    data class WorkerEvent (val sender:String, val name:String, val state:String, val worker:Worker<*>?) : Event()
    data class ManagerEvent(val sender:String, val name:String, val state:String, val manager:Manager?) : Event()
}