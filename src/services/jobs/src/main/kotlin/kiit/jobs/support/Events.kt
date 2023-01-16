package kiit.jobs.support

import kiit.actors.Action
import kiit.actors.Message
import kiit.common.DateTime
import kiit.utils.events.Event
import kiit.common.Identity
import kiit.actors.Status
import kiit.common.ext.toStringMySql
import kiit.common.ids.ULIDs
import kiit.common.log.LogLevel
import kiit.jobs.Manager
import kiit.jobs.Context
import kiit.jobs.Task
import kiit.jobs.WorkerContext
import kiit.results.Codes

/**
 * Builds events using the @see[kiit.common.Event] model
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
    fun build(manager: Manager, name:String): Event {
        val queue = manager.jctx.queue?.name ?: "no-queue"
        val id = manager.jctx.id
        val status = manager.status()
        return build(id, status, name, "State changed", "job", queue, "")
    }


    /**
     * Builds an event for the worker
     */
    fun build(jctx: Context, wctx: WorkerContext, name:String): Event {
        val queue = jctx.queue?.name ?: "no-queue"
        val worker = wctx.worker
        val id = worker.id
        val status = worker.status()
        return build(id, status, name, "State changed", "wrk", queue, "")
    }


    /**
     * Builds an event for the worker
     */
    fun worker(manager: Manager, action: Action, id:Identity): Event {
        val status = manager.status()
        val operator = "WRK"
        val name = "${operator}_${action.name.toUpperCase()}"
        val finalName = when {
            name.length < 20 -> name.padEnd(20 - name.length)
            name.length > 20 -> name.substring(0, 20)
            else -> name
        }
        return build(id, status, finalName, "$operator command - $action", "cmd", operator.toLowerCase(), "")
    }


    /**
     * Builds a Event using the managermg/worker identity, status and other info.
     */
    fun build(id: Identity, status: Status, name:String, desc:String, source:String, target:String, value:String, fields:List<Triple<String, String, String>> = emptyFields): Event {
        val code = status.toCode()
        val tag = if(id.tags.isEmpty()) "" else id.tags.first()
        return Event(
            uuid = ULIDs.create().value,
            area = id.area,
            service = id.name,
            agent = id.agent.name,
            env = id.env,
            inst = id.instance,
            name = name,
            desc = desc,
            status = code,
            source = source,
            target = target,
            value = value,
            time = DateTime.now(),
            tag = tag,
            fields = fields
        )
    }


    fun record(mgr:Manager, name: String, cmd: Message<*>, value:String, desc: String? = null, task: Task? = null) {
        val event = Events.build(mgr, name)
        val info = listOf(
            "id" to cmd.id.toString(),
            "source" to event.source,
            "name" to event.name,
            "target" to event.target,
            "value" to value,
            "time" to event.time.toStringMySql(),
            "desc" to (desc ?: event.desc)
        )
        mgr.jctx.logger.log(LogLevel.Info, null, "JOB $name", info)
    }


    private fun Status.toCode(): kiit.results.Status {
        return when (this) {
            is Status.InActive -> Codes.INACTIVE
            is Status.Started -> Codes.STARTING
            is Status.Waiting -> Codes.WAITING
            is Status.Running -> Codes.RUNNING
            is Status.Paused -> Codes.PAUSED
            is Status.Stopped -> Codes.STOPPED
            is Status.Completed -> Codes.COMPLETE
            is Status.Failed -> Codes.ERRORED
            else         -> Codes.SUCCESS
        }
    }



    val emptyFields:List<Triple<String, String, String>> = listOf()
}
