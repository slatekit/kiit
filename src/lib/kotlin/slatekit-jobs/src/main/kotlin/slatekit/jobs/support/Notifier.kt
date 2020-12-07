package slatekit.jobs.support

import slatekit.common.Event
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.core.common.Emitter
import slatekit.jobs.Job
import slatekit.jobs.workers.WorkerContext

open class Notifier(val jobEvents: Emitter<Event> = Emitter<Event>(),
               val wrkEvents: Emitter<Event> = Emitter<Event>()) {

    /**
     * Notifies listeners of Job changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(job: Job) {
        val queue = job.ctx.queue?.name ?: "no-queue"
        val id = job.id
        val status = job.status()
        val event = toEvent(id, status, "State changed", "job", queue)
        jobEvents.emit(event)
        jobEvents.emit(event.status.name, event)
    }

    /**
     * Notifies listeners of worker changes using the @see[slatekit.common.Event] model
     */
    open suspend fun notify(job: JobContext, ctx: WorkerContext) {
        val queue = job.queue?.name ?: "no-queue"
        val worker = ctx.worker
        val id = worker.id
        val status = worker.status()
        val event = toEvent(id, status, "State changed", "wrk", queue)
        wrkEvents.emit(event)
        wrkEvents.emit(event.status.name, event)
    }

    /**
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
    protected open fun toEvent(id:Identity, status: Status, desc:String, source:String, target:String, fields:List<Triple<String, String, String>> = emptyFields):Event {
        val code = JobUtils.toCode(status)
        val tag = if(id.tags.isEmpty()) "" else id.tags.first()
        return Event(
            area   = id.area,
            name   = id.name,
            action = status.name,
            agent  = id.agent.name,
            env    = id.env,
            uuid   = id.instance,
            status = code,
            desc   = desc,
            source = source,
            target = target,
            tag    = tag,
            fields = fields
        )
    }

    private val emptyFields:List<Triple<String, String, String>> = listOf()
}
