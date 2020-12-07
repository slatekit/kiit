package slatekit.jobs

import slatekit.core.common.Emitter
import slatekit.jobs.support.JobContext
import slatekit.jobs.workers.WorkerContext

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
        val event = Event(job.id, name, "job", job.status(), job.ctx.queue?.name, listOf())
        jobEvents.emit(event)
        jobEvents.emit(event.status.name, event)
    }

    /**
     * Notifies listeners of worker changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(jctx: JobContext, wctx: WorkerContext, name:String = stateChanged) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val worker = wctx.worker
        val event = Event(wctx.id, name, "wrk", worker.status(), jctx.queue?.name, worker.info())
        wrkEvents.emit(event)
        wrkEvents.emit(event.status.name, event)
    }

    private val emptyFields:List<Triple<String, String, String>> = listOf()
}
