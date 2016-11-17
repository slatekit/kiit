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

import slate.common.app._
import slate.common.logging.LoggerBase
import slate.common._


/**
 * Slate Kit Task for performing short/long/continuously running operation as a background task.
 * While, actors are a great solution for async, queued message handling, this task
 * component provides more fine-grained control for cases where you want to start,
 * stop, pause and resume execution of code and only have a task work for a designated
 * amount of time it takes to complete some operation.
 * @param name
 */
class Task(name:String = "")
  extends AppLifeCycle
  with AppMetaSupport
  with AppStatusSupport
  with AppRunSupportExt
  with Runnable
{

  //protected var _state:TaskState  = null
  protected var _log:LoggerBase = null
  protected var _config:InputArgs       = null
  protected var _settings:TaskSettings  = null


  /**
   * initialize via settings
   * @param settings
   */
  def this(settings:TaskSettings) = {
    this("")
    _settings = settings
  }


  /**
   * runs the task by executing the exec and end life-cycle methods
   */
  override def run():Unit = {
    if(_settings == null){
      _settings = new TaskSettings()
    }

    // execute code
    exec()

    // close services
    end()
  }


  override def appMeta(): AppMeta = _appMeta


  /**
   * initialize this task and update current status
    *
    * @param args
   * @return
   */
  override def init(args:Option[Any]): Result[Boolean] =
  {
    _rawArgs = args
    moveToState(AppRunConst.INITIALIZE)
    onInit(args)
  }


  /**
   * execute this task and update current status.
    *
    * @return
   */
  override def exec(): Result[Any] =
  {
    moveToState(AppRunConst.STARTED)
    onExec()
  }


  /**
   * end this task and update current status
   */
  override def end(): Unit =
  {
    onEnd()
    moveToState(AppRunConst.END)
  }


  /**
   * moves the current state to the name supplied and performs a status update
    *
    * @param state
   * @return
   */
  override protected def moveToState(state:String):AppRunState = {
    _state = new AppRunState(status = state)

    // status update
    if ( _statusMap.contains(state)) {
      _statusMap(state)(None, None)
    }
    else {
      statusUpdate(state)
    }
    _state
  }


  /**
   * implementation of a status update
    *
    * @param msg
   * @param code
   * @param value
   */
  override def statusUpdate(msg:String, code:Int = 0, value:Option[Any] = None):Unit = {
    // implement
    println(msg)
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
  protected def onExec():Result[Any] =
  {
    ok()
  }


  /**
   * provided for subclass task and implementing end code in the derived class
   */
  protected def onEnd() : Unit =
  {
  }
}
