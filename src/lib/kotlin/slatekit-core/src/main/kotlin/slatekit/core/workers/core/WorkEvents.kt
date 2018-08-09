package slatekit.core.workers.core

import slatekit.common.log.Logger
import slatekit.core.workers.Group
import slatekit.core.workers.Worker


class WorkEvents(val callback:((WorkEvent) -> Unit)? = null, val logger:Logger? = null) {



    fun onStarted(group:Group, worker:Worker<*>) {
        onEvent(WorkEvent(group, worker, "started"))
    }


    fun onPaused(group:Group, worker:Worker<*>) {
        onEvent(WorkEvent(group, worker, "paused"))
    }


    fun onResumed(group:Group, worker:Worker<*>) {
        onEvent(WorkEvent(group, worker, "resumed"))
    }


    fun onStopped(group:Group, worker:Worker<*>) {
        onEvent(WorkEvent(group, worker, "stopped"))
    }


    fun onError(group:Group, worker:Worker<*>) {
        onEvent(WorkEvent(group, worker, "errored"))
    }


    fun onEvent(event:WorkEvent) {
        val group = event.group?.name ?: "default-group"
        val worker = event.worker
        logger?.info("Worker: $group.${worker.name}: ${event.state}")
        callback?.invoke(event)
    }
}