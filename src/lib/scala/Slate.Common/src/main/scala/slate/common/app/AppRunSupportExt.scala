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

import slate.common.results.ResultSupportIn

trait AppRunSupportExt extends ResultSupportIn {

  // TODO: Look at making this immutable somehow ?
  protected var _state = new AppRunState()
  protected var _pauseSeconds = 0


  /**
   * moves the current state to started and calls the internal execute method
    *
    * @return
   */
  def start(): AppRunState = {
    moveToState(AppRunConst.STARTED)
  }


  /**
   * moves the current state to stopped.
    *
    * @return
   */
  def stop(): AppRunState = {
    moveToState(AppRunConst.STOPPED)
  }


  /**
   * moves the current state to paused with a default time
    *
    * @param seconds
   * @return
   */
  def pause(seconds:Int = 60): AppRunState = {
    _pauseSeconds = seconds
    moveToState(AppRunConst.PAUSED)
  }


  /**
   * moves the current state to resumed
    *
    * @return
   */
  def resume():AppRunState = {
    moveToState(AppRunConst.RESUMED)
  }


  /**
    * moves the current state to waiting
    *
    * @return
    */
  def waiting(): AppRunState = {
    moveToState(AppRunConst.WAITING)
  }


  /**
   * gets the current status of the application
    *
    * @return
   */
  def status(): AppRunState = {
    _state
  }


  /**
    * whether this is started
    *
    * @return
    */
  def isStarted(): Boolean = {
    isState(AppRunConst.STARTED)
  }


  /**
    * whether this is stopped
    *
    * @return
    */
  def isStopped(): Boolean = {
    isState(AppRunConst.STOPPED)
  }


  /**
    * whether this is paused
    *
    * @return
    */
  def isPaused(): Boolean = {
    isState(AppRunConst.PAUSED)
  }


  /**
    * whether this is resumed
    *
    * @return
    */
  def isResumed(): Boolean = {
    isState(AppRunConst.RESUMED)
  }


  /**
    * whether this is waiting
    *
    * @return
    */
  def isWaiting(): Boolean = {
    isState(AppRunConst.WAITING)
  }


  /**
    * whether this is running ( started or resumed )
    *
    * @return
    */
  def isStartedOrResumed(): Boolean = {
    isState(AppRunConst.STARTED) || isState(AppRunConst.RESUMED)
  }


  /**
    * whether this is running ( started or resumed )
    *
    * @return
    */
  def isStartedResumedWaiting(): Boolean = {
    isState(AppRunConst.STARTED) || isState(AppRunConst.RESUMED) || isState(AppRunConst.WAITING)
  }


  /**
    * whether this is not running ( stopped or paused )
    *
    * @return
    */
  def isStoppedOrPaused(): Boolean = {
    isState(AppRunConst.STOPPED) || isState(AppRunConst.PAUSED)
  }


  /**
    * whether the current state is at the one supplied.
    *
    * @param stateName
    * @return
    */
  def isState(stateName:String): Boolean = {
    _state.status == stateName
  }


  protected def moveToState(state:String):AppRunState = {
    _state = new AppRunState("", status = state)
    _state
  }
}

