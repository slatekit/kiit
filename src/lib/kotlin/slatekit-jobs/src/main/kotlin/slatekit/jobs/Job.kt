package slatekit.jobs

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import slatekit.actors.*
import slatekit.common.*
import slatekit.common.ext.toStringMySql
import slatekit.common.log.LogLevel
import slatekit.jobs.slatekit.jobs.WorkRequest
import slatekit.jobs.slatekit.jobs.Worker
import slatekit.jobs.slatekit.jobs.WorkContext
import slatekit.jobs.support.Events
import slatekit.jobs.support.Coordinator
import slatekit.policy.Policy
import slatekit.jobs.support.*
import slatekit.results.Try

/**
 * A Job is the top level model in this Background Job/Task Queue system. A job is composed of the following:
 *
 * TERMS:
 * 1. Identity  : An id, @see[slatekit.common.Identity] to distinctly identify a job
 * 3. Task      : A single work item with a payload that a worker can work on. @see[slatekit.jobs.Task]
 * 2. Queue     : Interface for a Queue that workers can optional source tasks from
 * 4. Workers   : 1 or more @see[slatekit.jobs.workers.Worker]s that can work on this job
 * 5. Command   : Commands ( start | stop | pause | resume | delay ) to manage a job or individual worker
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
class Job(val jctx: Context) : Controlled<Task>(slatekit.actors.Context(jctx.id.id, jctx.scope),jctx.channel), Check, Controls {

    val workers = Workers(jctx)
    private val events = jctx.notifier.jobEvents
    private val control = Coordinator(this)
    private val work = control.work

    /**
     * Subscribe to @see[slatekit.common.Status] being changed
     */
    fun on(op: suspend (Event) -> Unit) = events.on(op)

    /**
     * Subscribe to @see[slatekit.common.Status] beging changed to the one supplied
     */
    fun on(status: Status, op: suspend (Event) -> Unit) = events.on(status.name, op)

    /**
     * Subscribe to @see[slatekit.common.Status] beging changed to the one supplied
     */
    fun onError(op: suspend (Event) -> Unit) = events.on(ERROR_KEY, op)


    /**
     * Get work context by worker identity
     */
    fun get(id: Identity): WorkContext? = workers[id.id]


    /**
     * Get work context by worker name
     */
    fun get(name: String): WorkContext? = workers[name]


    suspend fun close() {
        jctx.channel.close()
    }

    /**
     * Run the job by starting it first and then managing it by handling commands
     */
    suspend fun run() {
        start()
        manage()
    }


    override suspend fun control(action: Action, msg: String?, target: String): Feedback {
        return when (target.isEmpty()) {
            true -> {
                jctx.channel.send(Control(action, msg, target = target))
                Feedback(true, "job")
            }
            else -> {
                val id = jctx.workers.first { it.id.instance == target }.id
                jctx.channel.send(Control(action, msg, target = target))
                Feedback(true, "wrk")
            }
        }
    }

    /**
     * Listens to incoming commands
     */
    suspend fun manage() {
        work()
    }


    /**
     * logs/handle error state/condition
     */
    suspend fun error(currentStatus: Status, message: String) {
        jctx.logger.error("Error with job ${jctx.id.id} - $message")
    }


    private suspend fun manageJob(action: Action) {
        if (!validate(action)) {
            notify("CMD_ERROR")
            return
        }
        when (action) {
            is Action.Delay -> control.delay(30)
            is Action.Start -> control.start()
            is Action.Pause -> control.pause(30)
            is Action.Stop -> control.stop()
            is Action.Kill -> control.kill()
            is Action.Resume -> control.resume()
            is Action.Check -> control.check()
            is Action.Process -> {
                jctx.logger.info("Process action on job does nothing")
            }
        }
    }

    private suspend fun manageWork(cmd: Command.WorkerCommand) {
        if (cmd.action == Action.Process && !Rules.canWork(this.status())) {
            notify("CMD_WARN")
            return
        }
        when (cmd.action) {
            is Action.Delay -> one(cmd.identity, false) { work.delay(it, seconds = 30) }
            is Action.Start -> one(cmd.identity, false) { work.start(it) }
            is Action.Pause -> one(cmd.identity, false) { work.pause(it, seconds = 30) }
            is Action.Resume -> one(cmd.identity, false) { work.resume(it) }
            is Action.Stop -> one(cmd.identity, false) { work.stop(it) }
            is Action.Process -> run(cmd.identity, true) { work.work(it, nextTask(it.task)) }
            is Action.Check -> one(cmd.identity, true) { work.check(it) }
            is Action.Kill -> one(cmd.identity, true) { work.kill(it) }
        }
    }

    private suspend fun nextTask(empty: Task): Task {
        return when (jctx.queue) {
            null -> empty
            else -> jctx.queue.next() ?: empty
        }
    }


    private fun record(name: String, cmd: Command, desc: String? = null, task: Task? = null) {
        val event = Events.build(this, cmd)
        val info = listOf(
            "id" to cmd.id.toString(),
            "uuid" to cmd.identity.id,
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
    private suspend fun all(action: Action, newStatus: Status, op: suspend (WorkContext) -> Try<Status>) {
        val job = this
        this.move(newStatus)
        ctx.scope.launch {
            jctx.notifier.notify(job)
        }
        each(op)
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun each(op: suspend (WorkContext) -> Try<Status>) {
        jctx.workers.forEach { worker ->
            val wctx = workers[worker.id]
            wctx?.let {
                op(it)
            }
        }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun one(id: Identity, launch: Boolean, op: suspend (WorkContext) -> Try<Status>) {
        val wctx = workers[id]
        wctx?.let {
            op(it)
        }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun run(id: Identity, launch: Boolean, op: suspend (WorkContext) -> Unit) {
        val wctx = workers[id]
        wctx?.let {
            op(it)
        }
    }


    private fun validate(action: Action): Boolean {
        return when (this.status()) {
            Status.Killed -> action == Action.Check
            else -> true
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


        /**
         * Initialize with just a function that will handle the work
         * @sample
         *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
         *  val job2 = Job(Identity.job("signup", "email"), suspend {
         *      // do work here
         *      WResult.Done
         *  })
         */
        operator fun invoke(id: Identity, op: suspend () -> WResult, scope: CoroutineScope = Jobs.scope): Job {
            return Job(id, listOf(worker(op)), null, scope, listOf())
        }

        /**
         * Initialize with just a function that will handle the work
         * @sample
         *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
         *  val job2 = Job(Identity.job("signup", "email"), suspend { task ->
         *      println("task id=${task.id}")
         *      // do work here
         *      WResult.Done
         *  })
         */
        operator fun invoke(id: Identity, op: suspend (Task) -> WResult, queue: Queue? = null, scope: CoroutineScope = Jobs.scope, policies: List<Policy<WorkRequest, WResult>> = listOf()): Job {
            return Job(id, listOf(op), queue, scope, policies)
        }

        /**
         * Initialize with a list of functions to excecute work
         */
        operator fun invoke(id: Identity, ops: List<suspend (Task) -> WResult>, queue: Queue? = null, scope: CoroutineScope = Jobs.scope, policies: List<Policy<WorkRequest, WResult>> = listOf()): Job {
            return Job(Context(id, coordinator(), workers(id, ops), queue = queue, scope = scope, policies = policies))
        }

        /**
         * Initialize with just a function that will handle the work
         *  val id = Identity.job("signup", "email")
         *  val job1 = Job(id, EmailWorker(id.copy(tags = listOf("worker")))
         */
        operator fun invoke(id: Identity, worker: Worker<*>, queue: Queue? = null, scope: CoroutineScope = Jobs.scope,
                            policies: List<Policy<WorkRequest, WResult>> = listOf()): Job = Job(Context(id, coordinator(), listOf(worker), queue = queue, scope = scope, policies = policies))


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
