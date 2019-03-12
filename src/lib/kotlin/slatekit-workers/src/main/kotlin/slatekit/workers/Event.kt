package slatekit.workers

import slatekit.common.queues.QueueSource

sealed class Event {
    data class JobEvent    (val sender:String, val name:String, val state:String, val job:Job) : Event()
    data class QueueEvent  (val sender:String, val name:String, val state:String, val queue: QueueSource<*>?) : Event()
    data class WorkerEvent (val sender:String, val name:String, val state:String, val worker:Worker<*>?) : Event()
    data class ManagerEvent(val sender:String, val name:String, val state:String, val manager:Manager?) : Event()
}