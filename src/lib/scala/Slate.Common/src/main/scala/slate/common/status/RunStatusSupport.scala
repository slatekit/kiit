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


  /**
   * gets the current state of execution
   *
   * @return
   */
  def state(): RunState


  /**
   * gets the current status of the application
   *
   * @return
   */
  def status(): RunStatus


  /**
   * moves the current state to started and calls the internal execute method
    *
    * @return
   */
  def start(): RunStatus = moveToState(RunStateExecuting)


  /**
   * moves the current state to waiting
   *
   * @return
   */
  def waiting(): RunStatus = moveToState(RunStateWaiting)


  /**
   * moves the current state to paused with a default time
   *
   * @param seconds
   * @return
   */
  def pause(seconds:Int = 60): RunStatus = moveToState(RunStatePaused)


  /**
   * moves the current state to stopped.
    *
    * @return
   */
  def stop(): RunStatus = moveToState(RunStateStopped)


  /**
   * moves the current state to resumed
    *
    * @return
   */
  def resume():RunStatus = moveToState(RunStateExecuting)


  /**
   * moves the current state to complete
   *
   * @return
   */
  def complete(): RunStatus = moveToState(RunStateComplete)


  /**
   * moves the current state to failed
   *
   * @return
   */
  def failed(): RunStatus = moveToState(RunStateFailed)


  /**
   * whether this is started which could be any phase after not-started
   *
   * @return
   */
  def isStarted(): Boolean = state().value > RunStateNotStarted.value


  /**
    * whether this is executing
    *
    * @return
    */
  def isExecuting(): Boolean = isState(RunStateExecuting)


  /**
   * whether this is waiting
   *
   * @return
   */
  def isWaiting(): Boolean = isState(RunStateWaiting)


  /**
   * whether this is paused
   *
   * @return
   */
  def isPaused(): Boolean = isState(RunStatePaused)


  /**
    * whether this is stopped
    *
    * @return
    */
  def isStopped(): Boolean = isState(RunStateStopped)


  /**
   * whether this is complete
   *
   * @return
   */
  def isComplete(): Boolean = isState(RunStateComplete)


  /**
   * whether this has failed
   *
   * @return
   */
  def isFailed(): Boolean = isState(RunStateFailed)


  /**
    * whether this is not running ( stopped or paused )
    *
    * @return
    */
  def isStoppedOrPaused(): Boolean = isState(RunStateStopped) || isState(RunStatePaused)


  /**
    * whether the current state is at the one supplied.
    *
    * @param runState
    * @return
    */
  def isState(runState:RunState): Boolean = state() == runState


  /**
   * moves this state to the one supplied
   * @param state
   * @return
   */
  protected def moveToState(state:RunState):RunStatus
}

