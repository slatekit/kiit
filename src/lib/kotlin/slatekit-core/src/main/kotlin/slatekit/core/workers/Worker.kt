package slatekit.core.workers

import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.info.About
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.status.*
import java.util.*
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
 *
 * GROUP:
 * A group is a collection of workers.
 * There is always at least 1 group in the system ( the default group )
 *
 * Features:
 *     1. Workers    : multiple workers can be part of a group
 *     2. Pause      : pause the entire group of workers
 *     3. Distribute : distribution of work to workers that are idle
 *     4. Status     : get a summary of the work status of each worker
 *
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
 */
open class Worker<T>(
        val metadata: WorkerMetadata    = WorkerMetadata(),
        val settings: WorkerSettings    = WorkerSettings(),
        val notifier: WorkNotification? = null,
        val callback: WorkFunction<T> ? = null

) : RunStatusSupport, Runnable {

    constructor(name:String     ,
                desc:String = "",
                notifier: WorkNotification? = null,
                callback: WorkFunction<T> ? = null):
            this(WorkerMetadata(About.simple(name, name, desc, "", "1.0")), notifier = notifier, callback = callback)

    protected val _runState = AtomicReference<RunState>(RunStateNotStarted)
    protected val _runStatus = AtomicReference<RunStatus>(RunStatus())
    protected val _runDelay = AtomicReference<Int>(0)
    protected val _lastResult = AtomicReference<Result<T>>(ResultFuncs.failure("not started"))


    val name:String = metadata.about.name


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
     * gets the last result from doing work.
     */
    val lastResult: Result<T> get() = _lastResult.get()


    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    override fun moveToState(state: RunState): RunStatus {
        val last = _runStatus.get()
        _runState.set(state)
        _runStatus.set(RunStatus(metadata.about.name, DateTime.now(), state.mode, last.runCount + 1, last.errorCount, ""))
        notifier?.let { it(_runStatus.get(), _lastResult.get()) }
        return _runStatus.get()
    }


    /**
     * initialize this task and update current status
     * @return
     */
    fun init(): Result<Boolean> {
        moveToState(RunStateInitializing)
        return onInit()
    }


    /**
     * This is to make this compatible with java runnable so that
     * the worker can be supplied to an ExectureService
     */
    override fun run() {
        work()
    }


    /**
     * execute this task and update current status.
     *
     * @return
     */
    open fun work(): Result<T> {
        moveToState(RunStateBusy)
        val result = try {
            val attempt = processInternal(null)
            _lastResult.set(attempt)
            attempt
        }
        catch(ex:Exception) {
            val last = _runStatus.get()
            _runStatus.set(
                RunStatus(
                    name        = metadata.about.name,
                    lastRunTime = DateTime.now(),
                    status      = RunStateBusy.mode,
                    runCount    = last.runCount + 1,
                    errorCount  = last.errorCount + 1,
                    lastResult  = ""
                )
            )
            failure<T>("Unexpected error : " + ex.message)
        }
        moveToState(RunStateIdle)
        return result
    }


    /**
     * end this task and update current status
     */
    fun end(): Unit {
        onEnd()
        moveToState(RunStateComplete)
    }


    /**
     * provided for subclass task and implementing initialization code in the derived class
     * @param args
     * @return
     */
    protected open fun onInit(): Result<Boolean> {
        return ResultFuncs.ok()
    }


    /**
     * provided for subclass task and implementing end code in the derived class
     */
    protected open fun onEnd(): Unit {
    }


    protected open fun process(args:Array<Any>?): Result<T> {
        return ResultFuncs.notImplemented()
    }


    private fun processInternal(args:Array<Any>?): Result<T> {
        return when (this) {
            is Queued<*> -> this.processQueue() as Result<T>
            else         -> callback?.let{ it(null) } ?: process(null)
        }
    }
}



open class WorkerWithQueue<T>(
                            val queue       : QueueSource,
                            metadata        : WorkerMetadata    = WorkerMetadata(),
                            settings        : WorkerSettings    = WorkerSettings(),
                            notifier        : WorkNotification? = null,
                            val callbackItem: ((T) -> Unit) ? = null

                        ) : Worker<T>(metadata, settings, notifier, null), Queued<T>
{

    constructor(name:String,
                desc:String,
                queue: QueueSource,
                notifier: WorkNotification? = null,
                settings: WorkerSettings? = null,
                callback: ((T) -> Unit) ? = null):
    this(queue, WorkerMetadata(About.simple(name, name, desc, "", "1.0")), settings ?: WorkerSettings(), notifier, callback)


    override fun queue(): QueueSource = queue


    override fun worker(): Worker<T>  = this


    /**
     * processes a single item. derived classes should implement this.
     *
     * @param item
     */
    override fun <R> processItem(item: R): Unit {
        callbackItem?.let { cb ->
            cb.invoke( item as T )
        } ?: super.processItem(item)
    }
}
