package slatekit.workers.core

import slatekit.common.log.Logger
import slatekit.workers.Worker

/**
 * Handles various events from the Manager/Worker such as status changes
 */
class Events(val callback: ((Event) -> Unit)? = null, val logger: Logger? = null) {

    fun onWorkerEvent(sender: Any, worker: Worker<*>, etype:EventType) {
        onEvent(Event(sender, worker, etype.name))
    }

    fun onJobEvent(sender:Any, worker:Worker<*>, etype:EventType) {
        onEvent(Event(sender, worker, etype.name))
    }

    fun onEvent(event: Event) {
        val worker = event.worker
        logger?.info("Worker: ${worker.about.name}: ${event.state}")
        callback?.invoke(event)
    }
}
