package slatekit.jobs.support

import slatekit.common.Emitter
import slatekit.jobs.Event
import slatekit.jobs.Manager
import slatekit.jobs.Worker

/**
 * Notification emitter for job and state changes.
 * Sends out events for changes to either job or a worker.
*/
open class Notifier(val jobEvents: Emitter<Event> = Emitter<Event>(),
                    val wrkEvents: Emitter<Event> = Emitter<Event>()) {
    private val stateChanged = "STATE_CHANGE"

    /**
     * Notifies listeners of Job changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(manager: Manager, eventName:String? = stateChanged) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val name = eventName ?: stateChanged
        val event = Event(manager.jctx.id, name, "State changed", "job", manager.status(), manager.jctx.queue?.name)
        jobEvents.emit(event)
        jobEvents.emit(event.status.name, event)
    }

    /**
     * Notifies listeners of worker changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(manager: Manager, worker: Worker<*>, eventName:String? = stateChanged) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val name = eventName ?: stateChanged
        val event = Event(worker.id, name, "State changed", "wrk", worker.status(), manager.jctx.queue?.name, info = worker.info())
        wrkEvents.emit(event)
        wrkEvents.emit(event.status.name, event)
    }
}
