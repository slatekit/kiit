package slatekit.jobs

import slatekit.common.Event
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.support.JobContext
import slatekit.jobs.support.JobUtils
import slatekit.jobs.workers.WorkerContext

/**
 * Builds events using the @see[slatekit.common.Event] model
 * to represent either a job/worker current state or for state changes.
 * This event model is also used for structured logging.
 *
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
object Events {

    /**
     * Builds an event for the job ( based on its current state )
     */
    fun build(job:Job): Event {
        val queue = job.ctx.queue?.name ?: "no-queue"
        val id = job.id
        val status = job.status()
        return build(id, status, "State changed", "job", queue)
    }


    /**
     * Builds an event for the worker
     */
    fun build(jctx:JobContext, wctx:WorkerContext): Event {
        val queue = jctx.queue?.name ?: "no-queue"
        val worker = wctx.worker
        val id = worker.id
        val status = worker.status()
        return build(id, status, "State changed", "wrk", queue)
    }


    /**
     * Builds a Event using the job/worker identity, status and other info.
     */
    fun build(id: Identity, status: Status, desc:String, source:String, target:String, fields:List<Triple<String, String, String>> = emptyFields): Event {
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



    val emptyFields:List<Triple<String, String, String>> = listOf()
}
