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
import slatekit.policy.Policy

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
class Manager(val jctx: Context, val settings: Settings = Settings())
    : Loader<Task>(Context(jctx.id.name, jctx.scope), jctx.channel, enableStrictMode = settings.isStrictlyPaused), Ops, Issuable<Task> {

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
    fun get(id: Identity): WorkerContext? = workers[id]


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
     * Supports direct issuing of messages
     * For internal/support/test purposes.
     * This should not be used for regular usage
     */
    override suspend fun issue(item: Message<Task>) {
        work(item)
    }


    /**
     *  Handles each message based on its type @see[Content], @see[Control],
     *  This handles following message types and moves this actor to a running state correctly
     *  1. @see[Control] messages to start, stop, pause, resume the actor
     *  2. @see[Request] messages to load payloads from a source ( e.g. queue )
     *  3. @see[Content] messages are simply delegated to the work method
     */
    override suspend fun work(item: Message<Task>) {
        val middleware = jctx.middleware
        when (item) {
            is Control -> {
                when(middleware){
                    null -> handle(item)
                    else -> middleware.handle(this,"JOBS", item) { handle(item) }
                }
            }
            is Request -> {
                state.begin(false)
                when(middleware){
                    null -> handle(item)
                    else -> middleware.handle(this,"JOBS", item) { handle(item) }
                }
            }
            else -> {
                // Does not support Request<T>
            }
        }
    }


    /**
     * Responds to changes in job state.
     * Notifies listeners and transitions all workers to the correct state.
     */
    override suspend fun onChanged(action: Action, oldStatus: Status, newStatus: Status) {
        // Notify listeners of job state change
        notify()

        // Transition all workers here
        val canChangeWorkers = when {
            action == Action.Kill -> true
            else -> state.validate(action)
        }
        if (canChangeWorkers) {
            all { process(action, 30, it) }
        }
    }


    /**
     * Handles a request for a @see[Task] and dispatches it to the default or target @see[Worker]
     */
    override suspend fun handle(req: Request<Task>) {
        // Id of worker
        val id = when (req.reference) {
            Message.NONE -> Workers.shortId(jctx.workers[0].id)
            else -> req.reference
        }

        // Run
        if(Rules.canWork(status())) {
            one(id) { run(it) }
        }
    }


    /**
     * Handles a request for a @see[Task] and dispatches it to the default or target @see[Worker]
     */
    private suspend fun handle(item: Control<Task>) {
        if (item.action == Action.Check) {
            val allDone = jctx.workers.all { it.isCompleted() }
            if (allDone) {
                complete()
            }
            return
        }
        when (item.reference) {
            Message.NONE -> state.handle(item.action)
            else -> manage(item)
        }
    }


    /**
     * Completes this job ( when all workers completed )
     */
    private suspend fun complete() {
        state.complete(false)
        notify()
    }


    /**
     * Manages a request to control a specific worker
     */
    private suspend fun manage(cmd: Control<Task>) {
        // Ensure only processing of running job
        if (cmd.action == Action.Process && !Rules.canWork(this.status())) {
            notify("CMD_WARN")
            return
        }
        one(cmd.reference) { process(cmd.action, cmd.seconds, it) }
    }


    private suspend fun process(action: Action, seconds: Long?, wctx: WorkerContext) {
        val finalSeconds = seconds ?: 30
        when (action) {
            is Action.Delay -> work.delay(wctx, seconds = finalSeconds)
            is Action.Start -> work.start(wctx)
            is Action.Pause -> work.pause(wctx, seconds = finalSeconds)
            is Action.Resume -> work.resume(wctx)
            is Action.Stop   -> work.stop(wctx)
            is Action.Process -> run(wctx)
            is Action.Check -> work.check(wctx)
            is Action.Kill -> work.kill(wctx)
        }
    }


    private suspend fun nextTask(empty: Task): Task {
        return when (jctx.queue) {
            null -> empty
            else -> jctx.queue.next() ?: empty
        }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun all(op: suspend (WorkerContext) -> Unit) {
        workers.contexts.forEach { op(it) }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun one(id: String, op: suspend (WorkerContext) -> Unit) {
        workers[id]?.let { op(it) }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun run(wctx:WorkerContext) {
        val task = nextTask(wctx.task)
        when(settings.isWorkLaunchable) {
            true -> jctx.scope.launch { work.work(wctx, task) }
            false -> work.work(wctx, task)
        }
    }


    private fun notify(eventName: String? = null) {
        val job = this
        ctx.scope.launch {
            jctx.notifier.notify(job, eventName)
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
                WorkerF<String>(id, op = it)
            }
        }

        fun coordinator(): Channel<Message<Task>> {
            return Channel(Channel.UNLIMITED)
        }

        /**
         * Initialize with just a function that will handle the work
         * @sample
         *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
         *  val job2 = Job(Identity.job("signup", "email"), suspend {
         *      // do work here
         *      WResult.Done
         *  })
         */
        operator fun invoke(id: Identity, op: suspend () -> WResult,
                            scope: CoroutineScope = Jobs.scope,
                            middleware: Middleware? = null,
                            settings: Settings = Settings()): Manager {
            return Manager(id, worker(op), null, scope, middleware, settings, listOf())
        }

        /**
         * Initialize with just a function that will handle the work
         * @param queue: Queue
         * @sample
         *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
         *  val job2 = Job(Identity.job("signup", "email"), suspend { task ->
         *      println("task id=${task.id}")
         *      // do work here
         *      WResult.Done
         *  })
         */
        operator fun invoke(id: Identity, op: suspend (Task) -> WResult,
                            queue: Queue? = null,
                            scope: CoroutineScope = Jobs.scope,
                            middleware: Middleware? = null,
                            settings: Settings = Settings(),
                            policies: List<Policy<WorkRequest, WResult>> = listOf()): Manager {
            return Manager(id, listOf(op), queue, scope, middleware, settings, policies)
        }

        /**
         * Initialize with a list of functions to excecute work
         */
        operator fun invoke(id: Identity, ops: List<suspend (Task) -> WResult>,
                            queue: Queue? = null,
                            scope: CoroutineScope = Jobs.scope,
                            middleware: Middleware? = null,
                            settings: Settings = Settings(),
                            policies: List<Policy<WorkRequest, WResult>> = listOf()): Manager {
            return Manager(Context(id, coordinator(), workers(id, ops), queue = queue, scope = scope, middleware = middleware, policies = policies), settings)
        }

        /**
         * Initialize with just a function that will handle the work
         *  val id = Identity.job("signup", "email")
         *  val job1 = Job(id, EmailWorker(id.copy(tags = listOf("worker")))
         */
        operator fun invoke(id: Identity, worker: Worker<*>,
                            queue: Queue? = null,
                            scope: CoroutineScope = Jobs.scope,
                            middleware: Middleware? = null,
                            settings: Settings = Settings(),
                            policies: List<Policy<WorkRequest, WResult>> = listOf()): Manager {
            return Manager(Context(id, coordinator(), listOf(worker), queue = queue, scope = scope, middleware = middleware, policies = policies), settings)
        }
    }
}
