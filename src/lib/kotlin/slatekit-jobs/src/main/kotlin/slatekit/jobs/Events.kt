package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.Event
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.ids.ULIDs
import slatekit.jobs.support.JobContext
import slatekit.jobs.support.JobUtils
import slatekit.jobs.workers.WorkerContext

/**
 * Builds events using the @see[slatekit.common.Event] model
 * to represent either a job/worker current state or for state changes.
 * This event model is used for structured logging.
 *
 * Event(
 *      uuid    = "uuid-1234",
 *      area    = "signup",
 *      service = "emails",
 *      agent   = "job",
 *      env     = "pro",
 *      inst    = "worker-001",
 *      name    = "JOB_STARTING",
 *      desc    = "State changed",
 *      status  = Codes.SUCCESS,
 *      source  = "wrk",
 *      target  = "queue://emails",
 *      tag     = "worker",
 *      time    = DateTime.now(),
 *      fields  = listOf(
 *          Triple( "region" , "usa"     , "" ),
 *          Triple( "device" , "android" , "" )
 *      )
 *  )
 */
object Events {

    /**
     * Builds an event for the job ( based on its current state )
     */
    fun build(job:Job, name:String): Event {
        val queue = job.ctx.queue?.name ?: "no-queue"
        val id = job.id
        val status = job.status()
        return build(id, status, name,"State changed", "job", queue)
    }


    /**
     * Builds an event for the worker
     */
    fun build(jctx:JobContext, wctx:WorkerContext, name:String): Event {
        val queue = jctx.queue?.name ?: "no-queue"
        val worker = wctx.worker
        val id = worker.id
        val status = worker.status()
        return build(id, status, name, "State changed", "wrk", queue)
    }


    /**
     * Builds a Event using the job/worker identity, status and other info.
     */
    fun build(id: Identity, status: Status, name:String, desc:String, source:String, target:String, fields:List<Triple<String, String, String>> = emptyFields): Event {
        val code = JobUtils.toCode(status)
        val tag = if(id.tags.isEmpty()) "" else id.tags.first()

        // JOB_STARTING | WRK_STARTING
        //val name = "${source.toUpperCase()}_${status.name.toUpperCase()}"
        return Event(
            uuid    = ULIDs.create().value,
            area    = id.area,
            service = id.name,
            agent   = id.agent.name,
            env     = id.env,
            inst    = id.instance,
            name    = name,
            desc    = desc,
            status  = code,
            source  = source,
            target  = target,
            time    = DateTime.now(),
            tag     = tag,
            fields  = fields
        )
    }



    val emptyFields:List<Triple<String, String, String>> = listOf()
}
