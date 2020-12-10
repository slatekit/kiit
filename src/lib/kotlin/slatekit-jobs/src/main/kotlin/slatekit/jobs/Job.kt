package slatekit.jobs

import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import slatekit.common.*
import slatekit.common.ext.toStringMySql
import slatekit.common.log.LogLevel
import slatekit.jobs.slatekit.jobs.Workers
import slatekit.jobs.slatekit.jobs.Ops
import slatekit.policy.Policy
import slatekit.jobs.support.*
import slatekit.jobs.workers.*
import slatekit.results.Outcome
import slatekit.results.Try
import slatekit.results.builders.Outcomes

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
class Job(val ctx: JobContext) : Ops<WorkerContext>, StatusCheck {

    /**
     * Initialize with just a function that will handle the work
     * @sample
     *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
     *  val job2 = Job(Identity.job("signup", "email"), suspend {
     *      // do work here
     *      WorkResult.Done
     *  })
     */
    constructor(
        id: Identity,
        op: suspend () -> WorkResult,
        scope: CoroutineScope = Jobs.scope
    ) :
        this(id, listOf(worker(op)), null, scope, listOf())

    /**
     * Initialize with just a function that will handle the work
     * @sample
     *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
     *  val job2 = Job(Identity.job("signup", "email"), suspend { task ->
     *      println("task id=${task.id}")
     *      // do work here
     *      WorkResult.Done
     *  })
     */
    constructor(
        id: Identity,
        op: suspend (Task) -> WorkResult,
        queue: Queue? = null,
        scope: CoroutineScope = Jobs.scope,
        policies: List<Policy<WorkRequest, WorkResult>> = listOf()
    ) :
        this(id, listOf(op), queue, scope, policies)

    /**
     * Initialize with just a function that will handle the work
     *  val id = Identity.job("signup", "email")
     *  val job1 = Job(id, EmailWorker(id.copy(tags = listOf("worker")))
     */
    constructor(
        id: Identity,
        worker: Worker<*>,
        queue: Queue? = null,
        scope: CoroutineScope = Jobs.scope,
        policies: List<Policy<WorkRequest, WorkResult>> = listOf()
    ) :
        this(JobContext(id, coordinator(), listOf(worker), queue = queue, scope = scope, policies = policies))

    /**
     * Initialize with a list of functions to excecute work
     */
    constructor(
        id: Identity,
        ops: List<suspend (Task) -> WorkResult>,
        queue: Queue? = null,
        scope: CoroutineScope = Jobs.scope,
        policies: List<Policy<WorkRequest, WorkResult>> = listOf()
    ) :
        this(JobContext(id, coordinator(), workers(id, ops), queue = queue, scope = scope, policies = policies))

    val id: Identity = ctx.id
    val workers = Workers(ctx)
    private val events = ctx.notifier.jobEvents
    private val _status = AtomicReference<Status>(Status.InActive)
    private val control = Control(this)
    private val work = control.work

    /**
     * Subscribe to @see[slatekit.common.Status] being changed
     */
    fun on(op: suspend (Event) -> Unit) {
        events.on(op)
    }

    /**
     * Subscribe to @see[slatekit.common.Status] beging changed to the one supplied
     */
    fun on(status: Status, op: suspend (Event) -> Unit) {
        events.on(status.name, op)
    }

    /**
     * Gets the current @see[slatekit.common.Status] of the job
     */
    override fun status(): Status = _status.get()


    fun get(id: Identity): WorkerContext? = workers[id.id]
    override fun get(name: String): WorkerContext? = workers[name]

    suspend fun close() {
        ctx.channel.close()
    }

    /**
     * Run the job by starting it first and then managing it by listening for requests
     */
    suspend fun run() {
        start()
        manage()
    }

    /**
     * Requests an action on the entire job
     */
    override suspend fun send(action: Action): Outcome<String> {
        return send(ctx.commands.job(ctx.id, action))
    }

    /**
     * Requests an action on a specific worker
     */
    override suspend fun send(id: Identity, action: Action, note: String): Outcome<String> {
        return when (JobUtils.isWorker(id)) {
            false -> send(ctx.commands.job(id, action))
            true -> send(ctx.commands.work(id, action))
        }
    }

    /**
     * Requests this job to perform the supplied command
     * Coordinator handles requests via kotlin channels
     */
    override suspend fun send(command: Command): Outcome<String> {
        ctx.channel.send(command)
        return when (JobUtils.isWorker(command.identity)) {
            true -> Outcomes.success("Sent command=${command.action.name}, type=job, target=${ctx.id.id}")
            false -> Outcomes.success("Send command=${command.action.name}, type=wrk, target=${ctx.id.id}")
        }
    }


    /**
     * Listens to and handles X commands
     */
    suspend fun pull(count: Int = 1) {
        // Process X off the channel
        for (x in 1..count) {
            val command = ctx.channel.poll()
            command?.let { cmd ->
                record("PULL", cmd)
                handle(cmd)
            }
        }
    }

    /**
     * Listens to and handles commands until there are no more
     */
    suspend fun poll(){
        var cmd: Command? = ctx.channel.poll()
        while(cmd != null) {
            record("POLL", cmd)
            handle(cmd)
            cmd = ctx.channel.poll()
        }
    }

    /**
     * Listens to incoming commands
     */
    suspend fun manage() {
        for (cmd in ctx.channel) {
            record("MNGR", cmd)
            handle(cmd)
            yield()
        }
    }

    suspend fun handle(command: Command) {
        when (command) {
            // Affects the whole job/queue/workers
            is Command.JobCommand -> {
                manageJob(command)
            }

            // Affects just a specific worker
            is Command.WorkerCommand -> {
                manageWork(command)
            }
        }
    }

    /**
     * Converts the name supplied to either the identity of either
     * 1. Job    : {identity.area}.{identity.service}                       e.g. "signup.emails"
     * 2. Worker : {identity.area}.{identity.service}.{identity.instance}   e.g. "signup.emails.worker_1"
     */
    override fun toId(name: String): Identity? {
        if (name.isBlank()) return null
        val parts = name.split(".")
        return when (parts.size) {
            2 -> ctx.id
            3 -> workers.getIds().first { it.instance == name }
            else -> null
        }
    }

    /**
     * logs/handle error state/condition
     */
    suspend fun error(currentStatus: Status, message: String) {
        val id = ctx.workers.first()
        ctx.logger.error("Error with job ${id.id.name}: $message")
    }


    internal fun move(newStatus: Status) {
        _status.set(newStatus)
    }


    private suspend fun manageJob(request: Command.JobCommand) {
        when (request.action) {
            is Action.Delay   -> control.delay(30)
            is Action.Start   -> control.start()
            is Action.Pause   -> control.pause(30)
            is Action.Stop    -> control.stop()
            is Action.Kill    -> control.kill()
            is Action.Resume  -> control.resume()
            is Action.Check   -> control.check()
            is Action.Process -> {
                ctx.logger.info( "Process action on job does nothing")
            }
        }
    }

    private suspend fun manageWork(command: Command.WorkerCommand) {
        when (command.action) {
            is Action.Delay   -> one(command.identity,false ) { work.delay( it, seconds = 30) }
            is Action.Start   -> one(command.identity,false ) { work.start( it) }
            is Action.Pause   -> one(command.identity,false ) { work.pause( it, seconds = 30) }
            is Action.Stop    -> one(command.identity,false ) { work.stop ( it) }
            is Action.Resume  -> one(command.identity,false ) { work.resume(it) }
            is Action.Check   -> one(command.identity,false ) { work.notify(null, id) }
            is Action.Process -> run(command.identity,true  ) { work.work  (it, nextTask(it.task)) }
        }
    }

    private suspend fun nextTask(empty: Task): Task {
        return when (ctx.queue) {
            null -> empty
            else -> ctx.queue.next() ?: empty
        }
    }


    private fun record(name: String, cmd: Command, desc: String? = null, task: Task? = null) {
        val event = Events.build(this, cmd)
        val info = listOf(
            "id" to cmd.identity.id,
            "source" to event.source,
            "name" to event.name,
            "target" to event.target,
            "time" to event.time.toStringMySql(),
            "desc" to (desc ?: event.desc)
        )
        ctx.logger.log(LogLevel.Info, "JOB $name", info)
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun all(action: Action, newStatus: Status, op:suspend(WorkerContext) -> Try<Status>) {
        val job = this
        this.move(newStatus)
        ctx.scope.launch {
            ctx.notifier.notify(job)
        }
        each(op)
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun each(op:suspend(WorkerContext) -> Try<Status>) {
        ctx.workers.forEach { worker ->
            val wctx = workers[worker.id]
            wctx?.let {
                op(it)
            }
        }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun one(id:Identity, launch:Boolean, op:suspend(WorkerContext) -> Try<Status>) {
        val wctx = workers[id]
        wctx?.let {
            op(it)
        }
    }


    /**
     * Transitions all workers to the new status supplied
     */
    private suspend fun run(id:Identity, launch:Boolean, op:suspend(WorkerContext) -> Unit) {
        val wctx = workers[id]
        wctx?.let {
            op(it)
        }
    }


    companion object {

        fun worker(call: suspend () -> WorkResult): suspend (Task) -> WorkResult {
            return { t ->
                call()
            }
        }

        fun workers(id: Identity, lamdas: List<suspend (Task) -> WorkResult>): List<Worker<*>> {
            val idInfo = when (id) {
                is SimpleIdentity -> id.copy(tags = listOf("worker"))
                else -> SimpleIdentity(id.area, id.service, id.agent, id.env, id.instance, listOf("worker"))
            }
            return lamdas.map {
                Worker<String>(idInfo.newInstance(), operation = it)
            }
        }

        fun coordinator(): Channel<Command> {
            return Channel(Channel.UNLIMITED)
        }
    }
}
