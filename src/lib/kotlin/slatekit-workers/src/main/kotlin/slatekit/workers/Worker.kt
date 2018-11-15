package slatekit.workers

import slatekit.common.*
import slatekit.common.info.About
import slatekit.common.log.Logs
import slatekit.common.metrics.Metrics
import slatekit.common.metrics.MetricsLite
import slatekit.common.queues.QueueSource
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
        val middleware: Middleware = Middleware(),
        val callback: WorkFunction<T>? = null

) : StatusSupport {

    private val _runState = AtomicReference<Status>(Status.InActive)
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
     * Metric used for reporting
     */
    val metricId:String = name.toId()


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
    val diagnostics = Diagnostics(metricId, metrics, log)


    /**
     * gets the current state of execution
     *
     * @return
     */
    override fun state(): Status = _runState.get()

    /**
     * gets the current status of the application
     *
     * @return
     */
    override fun status(): Status = _runStatus.get().status

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
        moveToState(Status.Starting)
        return onInit()
    }

    /**
     * end this task and update current status
     */
    fun end() {
        onEnd()
        moveToState(Status.Complete)
    }

    /**
     * For batching purposes
     */
    open fun work(sender: Any, batch: Batch) {
        val jobs = batch.jobs
        val queueInfo = batch.queue
        val queueSource = batch.queue.queue
        if (!jobs.isEmpty()) {
            jobs.forEach { job ->

                // Attempt to work on the job
                val result = Result.attempt { work(job) }

                // Acknowledge/Abandon
                complete(sender, queueSource, job, result)

                // Track all diagnostics
                diagnostics.record(this, WorkRequest(job, queueInfo, this), result.toResponse())
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
        moveToState(Status.Running)
        _lastRunTime.set(DateTime.now())

        val result = middleware.run(this, job) {
            //performLog("performing job : ${job.id}  ${job.queue}") {
                callback?.invoke(job) ?: perform(job)
            //}
        }
        _lastResult.set(result)
        moveToState(Status.Idle)
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
        val lastRequest  = diagnostics.tracker?.lastRequest?.get()
        val lastFiltered = diagnostics.tracker?.lastFiltered?.get()
        val lastSuccess  = diagnostics.tracker?.lastSuccess?.get()
        val lastErrored  = diagnostics.tracker?.lastFailure?.get()
        return Stats(
                about.id,
                about.name,
                status = _runState.get(),
                lastRunTime    = _lastRunTime.get(),
                lastResult     = _lastResult.get(),
                totalRequests  = metrics.total("$metricId.total_successes").toLong(),
                totalSuccesses = metrics.total("$metricId.total_filtered").toLong(),
                totalErrored   = metrics.total("$metricId.total_failed").toLong(),
                totalFiltered  = metrics.total("$metricId.total_other").toLong(),
                lastRequest    = lastRequest,
                lastFiltered   = lastFiltered,
                lastSuccess    = lastSuccess,
                lastErrored    = lastErrored
        )
    }

    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    override fun moveToState(state: Status): Status {
        val last = _runStatus.get()
        _runState.set(state)
        _runStatus.set(RunStatus(about.id, about.name, DateTime.now(), state))
        diagnostics.logger?.info("$metricId moving to state: " + state.name)
        return state
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
