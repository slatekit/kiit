/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.app

import slate.common.results.ResultCode

trait AppStatusSupport {

  protected val _statusMap:Map[String,(Option[String], Option[Any]) => Unit] =
    Map[String, (Option[String], Option[Any]) => Unit](
      AppRunConst.INITIALIZE -> statusInitialized,
      AppRunConst.EXECUTE    -> statusExecuting,
      AppRunConst.WAITING    -> statusWaiting,
      AppRunConst.END        -> statusEnded,
      AppRunConst.SUCCESS    -> statusSuccess,
      AppRunConst.FAILURE    -> statusFailure,
      AppRunConst.STARTED    -> statusStarted,
      AppRunConst.STOPPED    -> statusStopped,
      AppRunConst.PAUSED     -> statusPaused,
      AppRunConst.RESUMED    -> statusResumed
  )


  def statusInitialized(msg:Option[String] = Some(AppRunConst.INITIALIZE), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.INITIALIZE)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusExecuting(msg:Option[String] = Some(AppRunConst.STARTED), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.STARTED)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusWaiting(msg:Option[String] = Some(AppRunConst.WAITING), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.WAITING)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusEnded(msg:Option[String] = Some(AppRunConst.END), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.END)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusSuccess(msg:Option[String] = Some(AppRunConst.SUCCESS), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.SUCCESS)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusFailure(msg:Option[String] = Some(AppRunConst.FAILURE), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.FAILURE)
    statusUpdate(message, ResultCode.FAILURE, value)
  }


  def statusStarted(msg:Option[String] = Some(AppRunConst.STARTED), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.STARTED)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusStopped(msg:Option[String] = Some(AppRunConst.STOPPED), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.STOPPED)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusPaused(msg:Option[String] = Some(AppRunConst.PAUSED), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.PAUSED)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusResumed(msg:Option[String] = Some(AppRunConst.RESUMED), value:Option[Any] = None):Unit = {
    val message = msg.getOrElse(AppRunConst.RESUMED)
    statusUpdate(message, ResultCode.SUCCESS, value)
  }


  def statusUpdate(msg:String, code:Int = 0, value:Option[Any] = None):Unit = {
  }
}
