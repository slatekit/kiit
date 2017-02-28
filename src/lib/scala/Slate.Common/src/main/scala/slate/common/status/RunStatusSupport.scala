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

package slate.common.status

import slate.common.results.ResultSupportIn

trait RunStatusSupport extends ResultSupportIn {

  protected var _state = new RunStatus()
  protected var _pauseSeconds = 0


  /**
   * moves the current state to started and calls the internal execute method
    *
    * @return
   */
  def start(): RunStatus = moveToState(RunStateStarted)


  /**
   * moves the current state to stopped.
    *
    * @return
   */
  def stop(): RunStatus = moveToState(RunStateStopped)


  /**
   * moves the current state to paused with a default time
    *
    * @param seconds
   * @return
   */
  def pause(seconds:Int = 60): RunStatus = {
    this.synchronized {
      _pauseSeconds = seconds
    }
    moveToState(RunStatePaused)
  }


  /**
   * moves the current state to resumed
    *
    * @return
   */
  def resume():RunStatus = moveToState(RunStateResumed)


  /**
    * moves the current state to waiting
    *
    * @return
    */
  def waiting(): RunStatus = moveToState(RunStateWaiting)


  /**
   * gets the current status of the application
    *
    * @return
   */
  def status(): RunStatus = _state


  /**
    * whether this is started
    *
    * @return
    */
  def isStarted(): Boolean = isState(RunStateStarted)


  /**
    * whether this is stopped
    *
    * @return
    */
  def isStopped(): Boolean = isState(RunStateStopped)


  /**
    * whether this is paused
    *
    * @return
    */
  def isPaused(): Boolean = isState(RunStatePaused)


  /**
    * whether this is resumed
    *
    * @return
    */
  def isResumed(): Boolean = isState(RunStateResumed)


  /**
    * whether this is waiting
    *
    * @return
    */
  def isWaiting(): Boolean = isState(RunStateResumed)


  /**
    * whether this is running ( started or resumed )
    *
    * @return
    */
  def isStartedOrResumed(): Boolean = isState(RunStateStarted) || isState(RunStateResumed)


  /**
    * whether this is running ( started or resumed )
    *
    * @return
    */
  def isStartedResumedWaiting(): Boolean = isState(RunStateStarted) ||
    isState(RunStateResumed) || isState(RunStateWaiting)


  /**
    * whether this is not running ( stopped or paused )
    *
    * @return
    */
  def isStoppedOrPaused(): Boolean = isState(RunStateStopped) || isState(RunStatePaused)


  /**
    * whether the current state is at the one supplied.
    *
    * @param state
    * @return
    */
  def isState(state:RunState): Boolean = _state.status == state


  protected def moveToState(state:RunState):RunStatus = {
    this.synchronized {
      _state = new RunStatus("", status = state.mode)
      _state
    }
  }
}

