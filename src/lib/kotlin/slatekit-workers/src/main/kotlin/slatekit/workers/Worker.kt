package slatekit.workers

import slatekit.common.*
import slatekit.common.info.About
import slatekit.common.log.Logs
import slatekit.common.metrics.Metrics
import slatekit.common.metrics.MetricsLite
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultCode
import slatekit.common.results.ResultCode.NOT_IMPLEMENTED
import slatekit.workers.core.*
import slatekit.workers.status.*
import java.util.concurrent.atomic.AtomicReference

/**
 * The Slate Kit background worker system is composed of 3 parts:
 * 1. Worker       : A worker is the actual component that performs some work
 * 2. Group        : A group is simply a collection of workers
 * 3. Manager      : A coordinator checks on and manages the workers in a group
 * 4. System   : The top-most component containing workers/groups/coordinators
 *
 *
 * WORKER:
 * A worker performs some work and/or processes a single item from a work queue
 *
 * Features:
 *     1.  Life-cycle  : life-cycle events init, work, end
 *     2.  Interrupt   : can pause, stop and resume worker
 *     3.  States      : not-started, idle, working, paused, stopped, completed, failed
 *     4.  Status      : get the status of the worker ( last run time, error count etc )
 *     5.  Metadata    : get metadata about the worker ( name, desc, host, lang etc )
 *     6.  Events      : get notified every time the worker changes state
 *     7.  Setup       : either sub-class a Worker or supply a function to execute work
 *     8.  Ongoing     : configure worker as ongoing or 1 time run
 *     9.  Metrics     : basic metrics available on each worker such as start time
 *     10. Result      : saves the last processed result
 *     11. Type-safe   : strongly-typed return value
 *     12. Queues      : support for queue
 *
 *
 * MANAGER:
 * A manager manages the workers in a group. There is 1 manager per group.
 * This essentially boils down to periodically checking which workers are idle and either:
 * 1. non-queue based worker: simply calling the "work" method on the worker
 * 2. queue based worker    : getting items from the queue and providing them to the worker
 *
 * Features:
 * 1. strategy  : the default strategy for managing the workers is a round-robin
 * 2. timer     : configure the coordinator to check the workers at periodic intervals
 * 2. idle      : idle workers are made to work via calling the "work" method
 * 3. skip      : any workers that are paused | stopped | completed | failed are skipped
 *
 * @sample : Worker<String>("user.notifications", "notifications", "send notifications to users", "1.0"))
 */
open class Worker<T>(
        name: String,
        group: String,
        desc: String,
        version: String,
        val logs: Logs,
        val settings: WorkerSettings = WorkerSettings(),
        val metrics: Metrics = MetricsLite(),
        val tracker: Tracker = Tracker(DateTime.now()),
        val handler: Handler = Handler(DateTime.now()),
        val middleware: Middleware = Middleware(),
        val events: Events = Events(),
        val callback: WorkFunction<T>? = null

) : RunStatusSupport {

    private val _runState = AtomicReference<RunState>(RunStateNotStarted)
    private val _runStatus = AtomicReference<RunStatus>(RunStatus())
    private val _runDelay = AtomicReference<Int>(0)
    private val _lastResult = AtomicReference<ResultEx<T>>(Failure(Exception("not started")))
    private val _lastRunTime = AtomicReference<DateTime>(DateTime.MIN)

    /**
     * Unique id for this worker
     */
    val id = name + "." + Random.guid()

    /**
     * Information about this worker
     */
    val about: About = About(id, name, desc, group = group, version = version)

    /**
     * List of names of queues that this worker can handle jobs from
     */
    val queues: List<String> = listOf("*")


    /**
     * Logger for this worker ( separate from work system/manager )
     */
    val log = logs.getLogger(this.javaClass)


    /**
     * Diagnostics for full logs/metric/tracking/events
     */
    val diagnostics = Diagnostics(events, metrics, log, tracker)

    /**
     * Wraps an operation with useful logging indicating starting/completion of action
     */
    fun <T> performLog(name: String, action: () -> ResultEx<T>): ResultEx<T> {
        log.info("$name starting")
        val result = action()
        log.info("$name complete")
        return result
    }

    /**
     * gets the current state of execution
     *
     * @return
     */
    override fun state(): RunState = _runState.get()

    /**
     * gets the current status of the application
     *
     * @return
     */
    override fun status(): RunStatus = _runStatus.get()

    /**
     * Whether or not this worker is available for handling jobs
     */
    fun isAvailable(): Boolean {
        // Running indicates it is ready to handle jobs
        return isRunning() || isIdle()
    }

    /**
     * initialize this task and update current status
     * @return
     */
    fun init(): ResultMsg<Boolean> {
        moveToState(RunStateInitializing)
        return onInit()
    }

    /**
     * end this task and update current status
     */
    fun end() {
        onEnd()
        moveToState(RunStateComplete)
    }

    /**
     * For batching purposes
     */
    open fun work(sender: Any, batch: Batch) {
        val jobs = batch.jobs
        val queue = batch.queue.queue

        if (!jobs.isEmpty()) {
            jobs.forEach { job ->

                // Attempt to work on the job
                val result = Result.attempt { work(job) }

                // Acknowledge/Abandon
                complete(sender, queue, job, result)

                // Track all diagnostics
                diagnostics.record(this, queue, this, job, result)
            }
        }
    }

    /**
     * Works on the job while also handling metrics, middleware, events
     * @return
     */
    fun work(job: Job): ResultEx<T> {

        // Check current status
        if (isFailed() || isStopped() || isPaused()) {
            return _lastResult.get().toResultEx()
        }

        // Update state
        moveToState(RunStateRunning)
        _lastRunTime.set(DateTime.now())

        val result = middleware.run(this, job) {
            performLog("performing job : ${job.id}  ${job.queue}") {
                callback?.invoke(job) ?: perform(job)
            }
        }
        _lastResult.set(result)
        moveToState(RunStateIdle)
        return result
    }

    /**
     * execute this task and update current status.
     *
     * @return
     */
    open fun perform(job: Job): ResultEx<T> {
        return Failure(Exception("Not implemented"), NOT_IMPLEMENTED, "not implemented")
    }

    fun stats(): Stats {
        val lastRequest  = tracker.lastRequest.get()
        val lastFiltered = tracker.lastFiltered.get()
        val lastSuccess  = tracker.lastSuccess.get()
        val lastErrored  = tracker.lastErrored.get()

        return Stats(
                about.id,
                about.name,
                status = _runState.get(),
                lastRunTime    = _lastRunTime.get(),
                lastResult     = _lastResult.get(),
                totalRequests  = metrics.total("worker.total_successes").toLong(),
                totalSuccesses = metrics.total("worker.total_filtered").toLong(),
                totalErrored   = metrics.total("worker.total_failed").toLong(),
                totalFiltered  = metrics.total("worker.total_other").toLong(),
                lastRequest    = lastRequest.copy(source = lastRequest.source.javaClass.name),
                lastFiltered   = lastFiltered.copy(source = lastRequest.source.javaClass.name),
                lastSuccess    = lastSuccess.copy(first = lastSuccess.first.copy(source = lastRequest.source.javaClass.name)),
                lastErrored    = lastErrored.copy(first = lastSuccess.first.copy(source = lastRequest.source.javaClass.name))
        )
    }

    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    override fun moveToState(state: RunState): RunStatus {
        val last = _runStatus.get()
        _runState.set(state)
        _runStatus.set(RunStatus(about.id, about.name, DateTime.now(), state.mode))
        events.onEvent(Event(this.about.name, this, _runStatus.get().name))
        return _runStatus.get()
    }

    /**
     * provided for subclass task and implementing initialization code in the derived class
     * @param args
     * @return
     */
    protected open fun onInit(): ResultMsg<Boolean> {
        return Success(true)
    }

    /**
     * provided for subclass task and implementing end code in the derived class
     */
    protected open fun onEnd() {
    }


    protected open fun complete(sender: Any, queue: QueueSource, job:Job, result:ResultEx<*>) {
        when(result.success){
            true  -> queue.complete(job.source)
            false -> queue.abandon(job.source)
        }
    }
}
