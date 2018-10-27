package slatekit.core.workers.core

import slatekit.common.log.Logger
import slatekit.core.workers.Worker

/**
 * Handles various events from the Manager/Worker such as status changes
 */
class Events(val callback: ((Event) -> Unit)? = null, val logger: Logger? = null) {

    fun onStarted(sender: Any, worker: Worker<*>) {
        onEvent(Event(sender, worker, "started"))
    }

    fun onPaused(sender: Any, worker: Worker<*>) {
        onEvent(Event(sender, worker, "paused"))
    }

    fun onResumed(sender: Any, worker: Worker<*>) {
        onEvent(Event(sender, worker, "resumed"))
    }

    fun onStopped(sender: Any, worker: Worker<*>) {
        onEvent(Event(sender, worker, "stopped"))
    }

    fun onError(sender: Any, worker: Worker<*>) {
        onEvent(Event(sender, worker, "errored"))
    }

    fun onEvent(event: Event) {
        val worker = event.worker
        logger?.info("Worker: ${worker.about.name}: ${event.state}")
        callback?.invoke(event)
    }
}
