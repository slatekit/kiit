/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.core.tasks

import slatekit.common.DateTime
import slatekit.common.InputArgs
import slatekit.common.Result
import slatekit.common.app.AppLifeCycle
import slatekit.common.app.AppMeta
import slatekit.common.app.AppMetaSupport
import slatekit.common.log.LoggerBase
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.SUCCESS
import slatekit.common.status.*
import java.util.concurrent.atomic.AtomicReference


/**
 * Slate Kit interruptable Task for performing short/long/continuously running operation as a background task.
 * This can be used in conjunction with actors for fine-grained control
 * for cases where you want to start, pause, resume, stop execution of code
 *
 * NOTE: This decouples the scheduling from the processing.
 * @param name
 */
open class Task(name: String = "",
                protected val _settings: TaskSettings,
                protected val meta: AppMeta,
                protected val args: Any? = null,
                protected val _log: LoggerBase? = null,
                protected val _config: InputArgs? = null
)

    : AppLifeCycle
      , AppMetaSupport
      , RunStatusNotifier
      , RunStatusSupport
      , Runnable {

    protected val _runState = AtomicReference<RunState>(RunStateNotStarted)
    protected val _runStatus = AtomicReference<RunStatus>(RunStatus())
    protected val _runDelay = AtomicReference<Int>(0)


    /**
     * runs the task by executing the exec and end life-cycle methods
     */
    override fun run(): Unit {

        try {

            init()

            exec()

            end()
        }
        catch(ex: Exception) {
            moveToState(RunStateFailed)
        }
    }


    override fun appMeta(): AppMeta = meta


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
     * initialize this task and update current status
     * @return
     */
    override fun init(): Result<Boolean> {
        moveToState(RunStateInitializing)
        return onInit(args)
    }


    /**
     * execute this task and update current status.
     *
     * @return
     */
    override fun exec(): Result<Any> {
        moveToState(RunStateExecuting)
        return onExec()
    }


    /**
     * end this task and update current status
     */
    override fun end(): Unit {
        onEnd()
        moveToState(RunStateComplete)
    }


    /**
     * moves the current state to paused with a default time
     *
     * @param seconds
     * @return
     */
    override fun pause(seconds: Int): RunStatus {

        // Optimistic
        _runDelay.set(seconds)
        return moveToState(RunStatePaused)
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
        _runStatus.set(RunStatus(state.mode, DateTime.now(), state.mode, last.runCount + 1, last.runCount, ""))
        _runStatus.get()
        this.statusUpdate(_runState.get(), true, SUCCESS, null)
        return _runStatus.get()
    }


    /**
     * implementation of a status update
     *
     * @param code
     * @param message
     */
    override fun statusUpdate(state: RunState, success: Boolean, code: Int, message: String?): Unit {
        // implement
        println(message)
    }


    /**
     * provided for subclass task and implementing initialization code in the derived class
     * @param args
     * @return
     */
    protected open fun onInit(args: Any?): Result<Boolean> {
        return ok()
    }


    /**
     * provided for subclass task and implementing execution code in the derived class
     * @return
     */
    /**
     * executes this task by calling process and also checking
     * for any state transitions
     * @return
     */
    protected fun onExec(): Result<Any> {

        tailrec fun work(): RunState {

            // Process any items
            val workState = process()

            // e.g. paused, stopped, etc
            // NOTE: This will take priority of the result
            // of the workState via process
            return if (_runState.get() != RunStateExecuting)
                _runState.get()

            // e.g. waiting for work
            else if (workState != RunStateExecuting)
                workState

            // keep going - more to do
            else
                work()
        }

        // Begin and keep going until either:
        // 1. paused
        // 2. waiting
        // 3. stopped
        val result = work()
        return success(result)
    }


    /**
     * provided for subclass task and implementing end code in the derived class
     */
    protected open fun onEnd(): Unit {
    }


    /**
     * Should be implemented by derived classes
     * @return
     */
    protected open fun process(): RunState {

        // e.g. derived classes( such as a task queue / worker )
        // can process some items here, and instead of completing
        // can return a RunStateExecuting state.
        // See TaskQueue for more info.
        //
        // In this base class, we just return complete
        moveToState(RunStateComplete)
        return RunStateComplete
    }
}
