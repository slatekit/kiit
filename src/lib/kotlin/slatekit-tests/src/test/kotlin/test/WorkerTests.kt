package test

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.ListMap
import slatekit.common.Result
import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.notImplemented
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.common.status.*
import java.util.concurrent.atomic.AtomicReference

// https://stackoverflow.com/questions/2233561/producer-consumer-work-queues
// http://www.vogella.com/tutorials/JavaConcurrency/article.html
typealias WorkNotification = (RunStatus, Result<*>) -> Unit


typealias WorkFunction<T> = (Array<Any>?) -> Result<T>


data class WorkerMetrics(val name:String)


data class WorkerMetadata(val about:About = About.none, val host:Host = Host.local(), val lang:Lang = Lang.kotlin())


data class WorkerSettings(
                            val errorLimit         : Int     = 10,
                            val enableRestart      : Boolean = true,
                            val batchSize          : Int     = 10,
                            val isOngoing          : Boolean = false,
                            val waitTimeInSeconds  : Int     = 5,
                            val pauseTimeInSeconds : Int     = 5,
                            val stopTimeInSeconds  : Int     = 30
                          )

class WorkGroup
{
    private var _workers = ListMap<String, Worker<*>>()


    fun add(key:String, worker:Worker<*>):Unit {
        _workers = _workers.add(key, worker)
    }


    fun remove(key:String):Unit {
        _workers = _workers.remove(key)
    }


    val size:Int get() = _workers.size


    operator fun get(ndx:Int):Worker<*>? = _workers.getAt(ndx)


    operator fun get(key:String):Worker<*>? = _workers.get(key)


    val all:List<Worker<*>> get() = _workers.all()


    fun start():Unit = all.forEach{ it.start() }


    fun pause():Unit = all.forEach { it.pause() }


    fun resume():Unit = all.forEach{ it.resume() }


    fun stop():Unit = all.forEach{ it.stop() }

}


open class Manager ( val group:WorkGroup ) {
    open fun start():Unit {

    }
}


/**
 * The Slate Kit background worker system is composed of 3 parts:
 * 1. Worker       : A worker is the actual component that performs some work
 * 2. Group        : A group is simply a collection of workers
 * 3. Manager      : A coordinator checks on and manages the workers in a group
 * 4. WorkSystem   : The top-most component containing workers/groups/coordinators
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

) : RunStatusSupport {


    protected val _runState = AtomicReference<RunState>(RunStateNotStarted)
    protected val _runStatus = AtomicReference<RunStatus>(RunStatus())
    protected val _runDelay = AtomicReference<Int>(0)
    protected val _lastResult = AtomicReference<Result<T>>(ResultFuncs.failure("not started"))


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
    val lastResult:Result<T> get() = _lastResult.get()


    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    override fun moveToState(state: RunState): RunStatus {
        val last = _runStatus.get()
        _runState.set(state)
        _runStatus.set(RunStatus(state.mode, DateTime.now(), state.mode, last.runCount + 1, last.runCount, ""))
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
     * execute this task and update current status.
     *
     * @return
     */
    fun work(): Result<T> {
        moveToState(RunStateExecuting)
        val result = callback?.let{ it(null) } ?: process(null)
        _lastResult.set(result)
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
        return ok()
    }


    /**
     * provided for subclass task and implementing end code in the derived class
     */
    protected open fun onEnd(): Unit {
    }


    protected open fun process(args:Array<Any>?): Result<T> {
        return notImplemented()
    }
}



class MyWorker(var acc:Int = 0,
               notifier:WorkNotification? = null,
               callback: WorkFunction<Int>? = null) : Worker<Int>(notifier = notifier, callback = callback)
{
    var isInitialized = false
    var isEnded = false

    override fun onInit(): Result<Boolean> {
        isInitialized = true
        return super.onInit()
    }


    override fun onEnd() {
        isEnded = true
        super.onEnd()
    }


    override fun process(args:Array<Any>?): Result<Int> {
        acc += 1

        // Simulate different results for testing purposes
        return if(acc % 2 == 0 )
            success(acc, "even", "even")
        else
            success(acc, "odd", "odd")
    }
}


class TaskTests {

    @Test
    fun can_ensure_life_cycle(){
        val worker = MyWorker()
        worker.init()
        worker.work()
        worker.end()
        assert(worker.isInitialized)
        assert(worker.acc == 1)
        assert(worker.isEnded)
    }


    @Test
    fun can_ensure_not_started():Unit {
        assertState( {  }, RunStateNotStarted, false)
    }


    @Test
    fun can_use_lambda():Unit {
        var lambdaUsed = false
        val worker = MyWorker(callback = { lambdaUsed = true; success(1) })
        worker.work()
        assert(worker.acc == 0)
        assert(lambdaUsed)
    }


    @Test
    fun can_change_state_to_working():Unit {
        assertState( { it.start() }, RunStateWorking )
    }


    @Test
    fun can_change_state_to_paused():Unit {
        assertState( { it.pause() }, RunStatePaused )
    }


    @Test
    fun can_change_state_to_stopped():Unit {
        assertState( { it.stop() }, RunStateStopped )
    }


    @Test
    fun can_change_state_to_completed():Unit {
        assertState( { it.complete() }, RunStateComplete )
    }


    @Test
    fun can_send_status_notifications():Unit {
        assertNotifications( { it.complete() }, RunStateComplete )
    }


    @Test
    fun can_save_last_result():Unit {
        val worker = MyWorker(0)
        worker.work()
        assert(worker.lastResult.success)
        assert(worker.lastResult.msg == "odd")
        assert(worker.lastResult.code == slatekit.common.results.SUCCESS)
        assert(worker.lastResult.value == 1)

        worker.work()
        assert(worker.lastResult.success)
        assert(worker.lastResult.code == slatekit.common.results.SUCCESS)
        assert(worker.lastResult.msg == "even")
        assert(worker.lastResult.value == 2)
    }


    @Test
    fun can_work_once():Unit {
        val worker = MyWorker(0)
        val result = worker.work()
        assertResult(result, true, 1, slatekit.common.results.SUCCESS)
    }


    @Test
    fun can_work_multiple_times():Unit {
        val worker = MyWorker(0)
        worker.work()
        worker.work()
        val result = worker.work()
        assertResult(result, true, 3, slatekit.common.results.SUCCESS)
    }


    fun assertState(callback:(MyWorker) -> Unit, state:RunState, enableNotification:Boolean = true):Unit {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.state()
        assert(actual == state)
    }


    fun assertNotifications(callback:(MyWorker) -> Unit, state:RunState, enableNotification:Boolean = true):Unit {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.state()
        assert(actual == state)

        // Same test with notification
        var status: RunStatus? = null
        val worker2 = MyWorker(notifier = { s, r -> status = s })
        callback(worker2)
        val ac = worker2.state()
        assert(ac == state)
        assert(status != null)
        assert(status?.status == state.mode)
    }


    fun assertResult(result:Result<*>, success:Boolean, data:Any, code:Int){
        assert(result.success == success)
        assert(result.value == data)
        assert(result.code == code)
    }
}