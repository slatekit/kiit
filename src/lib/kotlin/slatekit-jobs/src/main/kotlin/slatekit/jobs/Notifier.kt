package slatekit.jobs

import slatekit.common.Event
import slatekit.core.common.Emitter
import slatekit.jobs.support.JobContext
import slatekit.jobs.workers.WorkerContext

/**
 * Notification emitter for job and state changes.
 * Sends out events for changes to either job or a worker.
 * Uses the @see[slatekit.common.Event] model
 * Event(
 *      area   = "signup",
 *      name   = "emails",
 *      action = "Starting",
 *      agent  = "job",
 *      env    = "pro",
 *      uuid   = "worker-001",
 *      status = Codes.SUCCESS,
 *      desc   = "State changed",
 *      source = "wrk",
 *      target = "queue://emails",
 *      tag    = "worker",
 *      fields = listOf(
 *          Triple( "region" , "usa"     , "" ),
 *          Triple( "device" , "android" , "" )
 *      )
 *  )
*/
open class Notifier(val jobEvents: Emitter<Event> = Emitter<Event>(),
               val wrkEvents: Emitter<Event> = Emitter<Event>()) {
    /**
     * Notifies listeners of Job changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(job: Job) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val event = Events.build(job)
        jobEvents.emit(event)
        jobEvents.emit(event.status.name, event)
    }

    /**
     * Notifies listeners of worker changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(jctx: JobContext, wctx: WorkerContext) {
        // Notify listeners interested in all (*) state changes
        // Notify listeners interested in only X state change
        val event = Events.build(jctx, wctx)
        wrkEvents.emit(event)
        wrkEvents.emit(event.status.name, event)
    }

    private val emptyFields:List<Triple<String, String, String>> = listOf()
}
