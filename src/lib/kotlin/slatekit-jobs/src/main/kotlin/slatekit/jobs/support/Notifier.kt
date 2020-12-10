package slatekit.jobs.slatekit.jobs.support

import slatekit.core.common.Emitter
import slatekit.jobs.Event
import slatekit.jobs.Job
import slatekit.jobs.workers.Worker

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
    open suspend fun notify(job: Job, name:String = stateChanged) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val event = Event(job.id, name, "State changed", "job", job.status(), job.ctx.queue?.name)
        jobEvents.emit(event)
        jobEvents.emit(event.status.name, event)
    }

    /**
     * Notifies listeners of worker changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(job: Job, worker: Worker<*>, name:String = stateChanged) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val event = Event(worker.id, name, "State changed", "wrk", worker.status(), job.ctx.queue?.name, info = worker.info())
        wrkEvents.emit(event)
        wrkEvents.emit(event.status.name, event)
    }
}
