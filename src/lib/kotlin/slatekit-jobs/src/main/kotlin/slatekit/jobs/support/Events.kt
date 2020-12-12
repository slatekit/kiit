package slatekit.jobs.support

import slatekit.actors.Action
import slatekit.common.DateTime
import slatekit.common.Event
import slatekit.common.Identity
import slatekit.actors.Status
import slatekit.common.ids.ULIDs
import slatekit.jobs.Job
import slatekit.jobs.Context
import slatekit.jobs.slatekit.jobs.WorkerContext

/**
 * Builds events using the @see[slatekit.common.Event] model
 * to represent either a job or worker's current state.
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
    fun build(job: Job, name:String): Event {
        val queue = job.jctx.queue?.name ?: "no-queue"
        val id = job.jctx.id
        val status = job.status()
        return build(id, status, name, "State changed", "job", queue)
    }


    /**
     * Builds an event for the worker
     */
    fun build(jctx: Context, wctx: WorkerContext, name:String): Event {
        val queue = jctx.queue?.name ?: "no-queue"
        val worker = wctx.worker
        val id = worker.id
        val status = worker.status()
        return build(id, status, name, "State changed", "wrk", queue)
    }


    /**
     * Builds an event for the worker
     */
    fun worker(job: Job, action:Action, id:Identity): Event {
        val status = job.status()
        val operator = "WRK"
        val name = "${operator}_${action.name.toUpperCase()}"
        val finalName = when {
            name.length < 20 -> name.padEnd(20 - name.length)
            name.length > 20 -> name.substring(0, 20)
            else -> name
        }
        return build(id, status, finalName, "$operator command - $action", "cmd" , operator.toLowerCase())
    }


    /**
     * Builds a Event using the job/worker identity, status and other info.
     */
    fun build(id: Identity, status: Status, name:String, desc:String, source:String, target:String, fields:List<Triple<String, String, String>> = emptyFields): Event {
        val code = status.toCode()
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
