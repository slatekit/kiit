package kiit.tasks

import kiit.common.Identity
import kiit.meta.kClass
import kiit.results.Err
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import kiit.utils.events.Emitter
import kotlin.reflect.KCallable

/**
 * One of the main components that actually handles the work
 * All work is done inside this worker which has
 * 1. life-cycle methods : InActive, Running, Paused,
 * 2. state changes      : @see[Status]
 * 3. eventing           : @see[Emitter] which sends events on state changes.
 * 4. diagnostic features: [info] method to build diagnostics info
 *
 * NOTES:
 * 1. All anonymous functions supplied to a Job are wrapped in a worker
 * 2. Clients should only extend worker to use/enrich the life-cycle, state change, alerting, diagnostic methods above
 */
open class Worker(val context: WorkContext, val events: Events = Events()) : Checks {
    private val action: Action = context.action
    private val executor = Executor()

    /**
     * ============================================================================
     * PROPERTIES
     * 1. status : for starting, pausing, etc
     * 2. note   : optional reason for any state changes
     * 3. info   : key / value pairs for diagnostics
     * ============================================================================
     */
    override fun status(): Status = action.status.get().status
    fun note(): String = action.status.get().note


    /**
     * Performs the work
     * @param task The task to perform.
     * NOTE: If this worker manages it's own work load/queue/source, then this task is
     * provided by the work() method and assigned Task.owned. Otherwise, the task is
     * supplied by the @see[kiit.jobs.Manager]
     */
    open suspend fun work(task: Task): Outcome<*> {
        // Work on the task
        val result = when(context.action.op != null) {
            true  -> executor.execLambda(context.action, task, context.action.op)
            false -> executor.execMethod(context.action, task, context.action.call!!.member)
        }
        // Emit result with context
        val actionResult = ActionResult(context.action, this, task, result.result, result.stats)
        events.results.emit(Events.EVENT_RESULT_READY, actionResult)
        return result.result
    }


    /**
     * ============================================================================
     * STATE MANAGEMENT
     * 1. move(status:Status) to transition status
     * 2. get status
     * ============================================================================
     */
    /**
     * Transition current status to the one supplied
     */
    open suspend fun control(command: Command, note: String? = null): Outcome<Status> {
        if(isFailed()) {
            return Outcomes.errored(Err.of("Worker state = failed"))
        }
        if(isCompleted()) {
            return Outcomes.errored(Err.of("Worker state = completed"))
        }

        val validStates = listOf(Status.Running, Status.Paused, Status.Ready, Status.InActive)
        return when(command) {
            Command.Pause  -> {
                Workflows.moveTo(this,Status.Paused, validStates) {}
            }
            Command.Resume -> {
                Workflows.moveTo(this,Status.Ready, validStates) {}
            }
            Command.Start  -> {
                Workflows.moveTo(this,Status.Ready, validStates) {}
            }
            Command.Stop   -> {
                Workflows.moveTo(this,Status.Stopped, validStates) {}
            }
        }
    }


    /**
     * ============================================================================
     * STATE MANAGEMENT
     * 1. move(status:Status) to transition status
     * 2. get status
     * ============================================================================
     */
    /**
     * Transition current status to the one supplied
     */
    suspend fun move(status: Status, note: String? = null) {
        action.status.set(State(status, note ?: status.name))
        events.workers.emit(Events.EVENT_WORKER_CHANGE, this)
    }

    companion object {

        fun adhoc(id:Identity, name:String, events: Events, op: KCallable<*>) : Worker {
            val call = Call(op.kClass, op, op)
            val action = Action(id, name, Mode.Adhoc, null, call)
            val context = WorkContext(action)
            val worker = Worker(context, events)
            return worker
        }

        fun adhocCall(id:Identity, name:String, events: Events, op: suspend (Task) -> Outcome<*>) : Worker {
            val action = Action(id, name, Mode.Adhoc, op, null)
            val context = WorkContext(action)
            val worker = Worker(context, events)
            return worker
        }

        fun repeat(id:Identity, name:String, options: Options, events: Events, op: KCallable<*>) : Worker {
            val call = Call(op.kClass, op, op)
            val action = Action(id, name, Mode.Repeat, op = null, call = call, options = options)
            val context = WorkContext(action)
            val worker = Worker(context, events)
            return worker
        }

        fun repeatCall(id:Identity, name:String, options: Options, events: Events, op: suspend (Task) -> Outcome<*>) : Worker {
            val action = Action(id, name, Mode.Repeat, op, options = options)
            val context = WorkContext(action)
            val worker = Worker(context, events)
            return worker
        }

        fun queued(id:Identity, name:String, queue: Queue, options:Options, events: Events, op: KCallable<*>) : Worker {
            val call = Call(op.kClass, op, op)
            val action = Action(id, name, Mode.Queued, op = null, call = call, options = options)
            val context = WorkContext(action, queue = queue)
            val worker = Worker(context, events)
            return worker
        }

        fun queuedCall(id:Identity, name:String, queue: Queue, options:Options, events: Events, op: suspend (Task) -> Outcome<*>) : Worker {
            val action = Action(id, name, Mode.Queued, op, options = options)
            val context = WorkContext(action, queue = queue)
            val worker = Worker(context, events)
            return worker
        }
    }
}