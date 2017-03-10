/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.tasks

import java.util.concurrent.atomic.AtomicReference

import slate.common.app._
import slate.common.logging.LoggerBase
import slate.common._
import slate.common.results.ResultCode
import slate.common.status._

import scala.annotation.tailrec


/**
 * Slate Kit interruptable Task for performing short/long/continuously running operation as a background task.
 * This can be used in conjunction with actors for fine-grained control
 * for cases where you want to start, pause, resume, stop execution of code
 *
 * NOTE: This decouples the scheduling from the processing.
 * @param name
 */
class Task(name:String = "",
           protected val _settings:TaskSettings,
           protected val meta:AppMeta,
           protected val args:Option[Any] = None,
           protected val _log:Option[LoggerBase] = None,
           protected val _config:Option[InputArgs] = None
          )

  extends AppLifeCycle
  with AppMetaSupport
  with RunStatusNotifier
  with RunStatusSupport
  with Runnable
{

  protected val _runState  = new AtomicReference[RunState](RunStateNotStarted)
  protected val _runStatus = new AtomicReference[RunStatus](new RunStatus())
  protected val _runDelay  = new AtomicReference[Int](0)


  /**
   * runs the task by executing the exec and end life-cycle methods
   */
  override def run():Unit = {

    try {

      init()

      exec()

      end()
    }
    catch{
      case ex:Exception => moveToState(RunStateFailed)
    }
  }


  override def appMeta(): AppMeta = meta


  /**
   * gets the current state of execution
   *
   * @return
   */
  override def state(): RunState = _runState.get


  /**
   * gets the current status of the application
   *
   * @return
   */
  override def status(): RunStatus = _runStatus.get


  /**
   * initialize this task and update current status
   * @return
   */
  override def init(): Result[Boolean] =
  {
    moveToState(RunStateInitializing)
    onInit(args)
  }


  /**
   * execute this task and update current status.
    *
    * @return
   */
  override def exec(): Result[Any] =
  {
    moveToState(RunStateExecuting)
    onExec()
  }


  /**
   * end this task and update current status
   */
  override def end(): Unit =
  {
    onEnd()
    moveToState(RunStateComplete)
  }


  /**
   * moves the current state to paused with a default time
   *
   * @param seconds
   * @return
   */
  override def pause(seconds:Int = 60): RunStatus = {

    // Optimistic
    _runDelay.set(seconds)
    moveToState(RunStatePaused)
  }


  /**
   * moves the current state to the name supplied and performs a status update
    *
    * @param state
   * @return
   */
  override protected def moveToState(state:RunState):RunStatus = {
    val last = _runStatus.get()
    _runState.set(state)
    _runStatus.set(new RunStatus(state.mode, DateTime.now(), state.mode, last.runCount + 1, last.runCount, ""))
    _runStatus.get()
    this.statusUpdate(_runState.get(), true, ResultCode.SUCCESS, None)
    _runStatus.get()
  }


  /**
   * implementation of a status update
    *
   * @param code
   * @param message
   */
  override def statusUpdate(state:RunState, success:Boolean, code:Int, message:Option[String]):Unit = {
    // implement
    println(message)
  }


  /**
   * provided for subclass task and implementing initialization code in the derived class
   * @param args
   * @return
   */
  protected def onInit(args:Option[Any]): Result[Boolean] = {
    ok()
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
  protected def onExec():Result[Any] =
  {
    @tailrec
    def work(): RunState = {

      // Process any items
      val workState = process()

      // e.g. paused, stopped, etc
      // NOTE: This will take priority of the result
      // of the workState via process
      if (_runState.get() != RunStateExecuting)
        _runState.get

      // e.g. waiting for work
      else if ( workState != RunStateExecuting )
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
    success(result)
  }


  /**
   * provided for subclass task and implementing end code in the derived class
   */
  protected def onEnd() : Unit = {
  }


  /**
   * Should be implemented by derived classes
   * @return
   */
  protected def process(): RunState = {

    // e.g. derived classes( such as a task queue / worker )
    // can process some items here, and instead of completing
    // can return a RunStateExecuting state.
    // See TaskQueue for more info.
    //
    // In this base class, we just return complete
    moveToState(RunStateComplete)
    RunStateComplete
  }
}
