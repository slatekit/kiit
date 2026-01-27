package kiit.tasks

import kiit.common.Identity
import kiit.results.Err
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KCallable


open class TaskService(val id:Identity, val scope: CoroutineScope, val events: Events = Events()) : Controls {

    private val actions: MutableMap<String, Worker> = mutableMapOf()
    private val queues : MutableMap<String, MutableMap<String, Worker>> = mutableMapOf()

    /**
     * Register an adhoc worker
     */
    suspend fun adhoc(name:String, op: KCallable<*>) {
        add {
            Worker.adhoc(id, name, events, op)
        }
    }


    /**
     * Register a repeating/scheduled worker
     */
    suspend fun repeat(name:String, options: Options, op: KCallable<*>) {
        val worker = add {
            Worker.repeat(id, name, options, events, op)
        }
        if(options.kickoff) {
            // Kick off the repeating job.
            exec(worker.context.action.fullName)
        }
    }


    /**
     * Register a queue processing worker.
     */
    suspend fun queued(name:String, queue: Queue, options:Options, op: KCallable<*>) {
        val worker = add {
            Worker.queued(id, name, queue, options, events, op)
        }
        if(options.kickoff) {
            // Kick off the repeating job.
            exec(worker.context.action.fullName)
        }
    }


    /**
     * Get all workers registered.
     */
    fun actions(): List<Action> {
        return this.actions.values.map { it.context.action }
    }


    /**
     * Get all workers registered.
     */
    fun workers(): List<Worker> {
        return actions.values.map { it }.toList()
    }


    /**
     * Gets the worker tied to the action name below.
     * This is not distributed* yet, but will be soon and so can be multiple workers.
     */
    fun getWorker(action:String): Worker? {
        return actions[action]
    }


    /**
     * Gets the status of
     * This is not distributed yet, but will be soon.
     */
    fun status(action:String): Outcome<Status> {
        return when(val worker = getWorker(action)) {
            null -> Outcomes.invalid(Err.on("action", action, "Not found"))
            else -> Outcomes.success(worker.status())
        }
    }


    /**
     * Controls the execution or life-cycle of the worker/process with the name supplied.
     * @param name    : The identity/name of the worker/process. Name of @see[kiit.common.Identity]
     * @param command : The command to use to control the life-cycle ( e.g. pause, resume )
     */
    override fun control(fullname: String, command: Command): Outcome<Status> {
        return Outcomes.invalid()
    }


    /**
     * Executes the action using the task supplied.
     * This is here to kick off the background task(s) using appropriate flow.
     */
    fun exec(action:String, task: Task? = null): Outcome<String> {
        val worker = getWorker(action)
        return when(worker) {
            null -> {
                Outcomes.invalid(Err.on("action", action, "Not found"))
            }
            else -> {
                val flow = Workflows.getWorkflow(worker.context, worker)
                val finalTask = task ?: worker.context.newTask("tasks.exec")
                scope.launch {
                    flow.process(finalTask)
                }
                Outcomes.success(action)
            }
        }
    }


    /**
     * Adds the worker ( which for now is a 1 to 1 relationship with the action ).
     * 1. Register action by name in the internal registry
     * 2. Register queue by name in the mapping of queues to actions/workers
     * 3. Emit the action to the events so listeners can know
     */
    private suspend fun add(op:() -> Worker) : Worker {
        val worker = op()

        // 1. Store action by name
        actions[worker.context.action.fullName] = worker
        val action = worker.context.action

        // 2. Map all the queues ( by name ) to the workers.
        if(action.mode == Mode.Queued ) {
            worker.context.queue?.let {
                val existing = queues[it.name]
                val map = when(existing == null) {
                    true -> HashMap<String, Worker>()
                    false -> queues[it.name]!!
                }
                // e.g. queue1 -> [
                //  { "app.tasks.action1" -> worker1 },
                //  { "app.tasks.action2" -> worker2 }
                // ]
                queues[it.name] = map

                // e.g. { "app.tasks.action1" -> worker1 },
                map[worker.context.action.fullName] = worker
            }
        }
        // 3. Event out the action
        events.actions.emit(Events.EVENT_ACTION_CHANGE, worker.context.action)
        return worker
    }

    companion object {
        val scope = CoroutineScope(Dispatchers.IO)
    }
}

