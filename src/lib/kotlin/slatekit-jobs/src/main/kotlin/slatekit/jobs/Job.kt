package slatekit.jobs

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import slatekit.actors.*
import slatekit.common.*
import slatekit.common.ext.toStringMySql
import slatekit.common.log.LogLevel
import slatekit.jobs.support.Events
import slatekit.jobs.support.Rules
import slatekit.jobs.support.Work
import slatekit.results.Try

/**
 * A Job is the top level model in this Background Job/Task Queue system. A job is composed of the following:
 *
 * TERMS:
 * 1. Identity  : An id, @see[slatekit.common.Identity] to distinctly identify a job
 * 3. Task      : A single work item with a payload that a worker can work on. @see[slatekit.jobs.Task]
 * 2. Queue     : Interface for a Queue that workers can optional source tasks from
 * 4. Workers   : 1 or more @see[slatekit.jobs.workers.Worker]s that can work on this job
 * 5. Action    : Actions ( start | stop | pause | resume | delay ) to control a job or individual worker
 * 6. Events    : Used to subscribe to events on the job/worker ( only status changes for now )
 * 7. Stats     : Reasonable statistics / diagnostics for workers such as total calls, processed, logging
 * 8. Policies  : @see[slatekit.policy.Policy] associated with a worker such as limits, retries
 * 9. Backoffs  : Exponential sequence of seconds to use to back off from processing queues when queue is empty
 * 10. Channel  : The mode of communication to send commands to a job for safe access to shared state
 *
 *
 * NOTES:
 * 1. Coordination is kept thread safe as all requests to manage / control a job are handled via channels
 * 2. A worker does not need to work from a Task Queue
 * 3. You can stop / pause / resume the whole job which means all workers are paused
 * 4. You can stop / pause / resume a single worker also
 * 5. Policies such as limiting the amount of runs, processed work, error rates, retries are done via policies
 * 6. The identity of a worker is based on the identity of its parent job + a workers uuid/unique
 * 7. A default implementation of the Queue is available in slatekit.integration.jobs.JobQueue
 *
 *
 * INSPIRED BY:
 * 1. Inspired by Ruby Rails SideKiq, NodesJS Bull, Python Celery
 * 2. Kotlin Structured Concurrency via Channels ( misc blog posts by Kotlin Team )
 * 3. Actor model see https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html
 *
 *
 * LIMITS:
 * 1. No database support ( e.g. for storing status/state/results in the database, etc )
 * 2. No distributed jobs support
 *
 *
 * FUTURE:
 * 1. Address the limits listed above
 * 2. Add support for Redis as a Queue
 * 3. Integration with Kotlin Flow ( e.g. a job could feed data into a Flow )
 *
 */
class Job(val jctx: Context)
    : Managed<Task>(slatekit.actors.Context(jctx.id.id, jctx.scope), jctx.channel), Issuer, Check, Ops {

    val workers = Workers(jctx)
    private val events = jctx.notifier.jobEvents
    private val work = Work(this)


    /**
     * Subscribe to any @see[slatekit.actors.Status] being changed
     */
    fun on(op: suspend (Event) -> Unit) = events.on(op)


    /**
     * Subscribe to @see[slatekit.actors.Status] beging changed to the one supplied
     */
    fun on(status: Status, op: suspend (Event) -> Unit) = events.on(status.name, op)


    /**
     * Subscribe to @see[slatekit.actors.Status] beging changed to the one supplied
     */
    fun onErr(op: suspend (Event) -> Unit) = events.on(ERROR_KEY, op)


    /**
     * Get worker context by identity
     */
    fun get(id: Identity): WorkerContext? = workers[id.id]


    /**
     * Get WorkerContext by worker name
     */
    fun get(name: String): WorkerContext? = workers[name]


    /**
     * Get WorkerContext by index
     */
    fun get(index: Int): WorkerContext? = workers.contexts[index]


    suspend fun close() {
        jctx.channel.close()
    }

    /**
     * Run the job by starting it first and then managing it by handling commands
     */
    suspend fun run(): kotlinx.coroutines.Job  {
        start()
        return work()
    }


    /**
     * Sends a request message
     * This represents a request to get payload internally ( say from a queue ) and process it
     * @param msg  : Full message
     */
    override suspend fun request() {
        request(Reference.NONE)
    }


    /**
     * Sends a request message associated with the supplied target
     * This represents a request to get payload internally ( say from a queue ) and process it
     * @param reference  : Full message
     */
    override suspend fun request(reference: String) {
        channel.send(Request(nextId(), reference = reference))
    }


    /**
     *  Handles each message based on its type @see[Content], @see[Control],
     *  This handles following message types and moves this actor to a running state correctly
     *  1. @see[Control] messages to start, stop, pause, resume the actor
     *  2. @see[Request] messages to load payloads from a source ( e.g. queue )
     *  3. @see[Content] messages are simply delegated to the work method
     */
    override suspend fun work(item: Message<Task>) {
        when (item) {
            is Request -> {
                begin(); handle(item)
            }
            is Control -> {
                handle(item)
            }
            is Content -> {
                begin(); handle(Action.Process, item.reference, item.data)
            }
        }
    }


    /**
     * Handles a request for a @see[Task] and dispatches it to the default or target @see[Worker]
     */
    private suspend fun handle(req: Request<Task>) {
        one(req.reference, true) {
            work.work(it, nextTask(it.task))
        }
    }


    /**
     * Handles a processing of a @see[Task] by dispatching it to the default or target @see[Worker]
     */
    override suspend fun handle(action: Action, target: String, item: Task) {
        one(target, true) {
            work.work(it, item)
        }
    }


    /**
     * logs/handle error state/condition
     */
    private fun error(message: String, ex:Exception? = null) {
        jctx.logger.error("Error with job ${jctx.id.id} - $message")
    }


    private suspend fun manage(cmd: Control<Task>) {
        if (cmd.action == Action.Process && !Rules.canWork(this.status())) {
            notify("CMD_WARN")
            return
        }
        when (cmd.action) {
            is Action.Delay -> one(cmd.reference, false) { work.delay(it, seconds = 30) }
            is Action.Start -> one(cmd.reference, false) { work.start(it) }
            is Action.Pause -> one(cmd.reference, false) { work.pause(it, seconds = 30) }
            is Action.Resume -> one(cmd.reference, false) { work.resume(it) }
            is Action.Stop -> one(cmd.reference, false) { work.stop(it) }
            is Action.Process -> run(cmd.reference, true) { work.work(it, nextTask(it.task)) }
            is Action.Check -> one(cmd.reference, true) { work.check(it) }
            is Action.Kill -> one(cmd.reference, true) { work.kill(it) }
        }
    }


    private suspend fun nextTask(empty: Task): Task {
        return when (jctx.queue) {
            null -> empty
            else -> jctx.queue.next() ?: empty
        }
    }


    private fun record(name: String, cmd: Message<Task>, desc: String? = null, task: Task? = null) {
        val event = Events.build(this, "message")
        val info = listOf(
            "id" to cmd.id.toString(),
            "source" to event.source,
            "name" to event.name,
            "target" to event.target,
            "time" to event.time.toStringMySql(),
            "desc" to (desc ?: event.desc)
        )
        jctx.logger.log(LogLevel.Info, "JOB $name", info)
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun one(id: String, launch: Boolean, op: suspend (WorkerContext) -> Try<Status>) {
        val wctx = workers[id]
        wctx?.let {
            op(it)
        }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun run(id: String, launch: Boolean, op: suspend (WorkerContext) -> Unit) {
        val wctx = workers[id]
        wctx?.let {
            op(it)
        }
    }


    private fun notify(name: String) {
        val job = this
        ctx.scope.launch {
            jctx.notifier.notify(job, name)
        }
    }


    companion object {

        const val ERROR_KEY = "Error"

        fun worker(call: suspend () -> WResult): suspend (Task) -> WResult {
            return { t ->
                call()
            }
        }

        fun workers(id: Identity, lamdas: List<suspend (Task) -> WResult>): List<Worker<*>> {
            return lamdas.map {
                Worker<String>(id, operation = it)
            }
        }

        fun coordinator(): Channel<Message<Task>> {
            return Channel(Channel.UNLIMITED)
        }
    }
}
