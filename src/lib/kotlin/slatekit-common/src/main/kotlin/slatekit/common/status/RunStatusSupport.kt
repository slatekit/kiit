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

package slatekit.common.status

interface RunStatusSupport {

    /**
     * gets the current state of execution
     *
     * @return
     */
    fun state(): RunState

    /**
     * gets the current status of the application
     *
     * @return
     */
    fun status(): RunStatus

    /**
     * moves the current state to idle.
     *
     * @return
     */
    fun start(): RunStatus = moveToState(RunStateIdle)

    /**
     * moves the current state to paused with a default time
     *
     * @param seconds
     * @return
     */
    fun pause(seconds: Int = 60): RunStatus = moveToState(RunStatePaused)

    /**
     * moves the current state to stopped.
     *
     * @return
     */
    fun stop(): RunStatus = moveToState(RunStateStopped)

    /**
     * moves the current state to resumed
     *
     * @return
     */
    fun resume(): RunStatus = moveToState(RunStateIdle)

    /**
     * moves the current state to complete
     *
     * @return
     */
    fun complete(): RunStatus = moveToState(RunStateComplete)

    /**
     * moves the current state to failed
     *
     * @return
     */
    fun fail(): RunStatus = moveToState(RunStateFailed)

    /**
     * whether this is executing
     *
     * @return
     */
    fun isRunning(): Boolean = isState(RunStateRunning)

    /**
     * whether this is waiting
     *
     * @return
     */
    fun isIdle(): Boolean = isState(RunStateIdle)

    /**
     * whether this is paused
     *
     * @return
     */
    fun isPaused(): Boolean = isState(RunStatePaused)

    /**
     * whether this is stopped
     *
     * @return
     */
    fun isStopped(): Boolean = isState(RunStateStopped)

    /**
     * whether this is complete
     *
     * @return
     */
    fun isComplete(): Boolean = isState(RunStateComplete)

    /**
     * whether this has failed
     *
     * @return
     */
    fun isFailed(): Boolean = isState(RunStateFailed)

    /**
     * whether this is not running ( stopped or paused )
     *
     * @return
     */
    fun isStoppedOrPaused(): Boolean = isState(RunStateStopped) || isState(RunStatePaused)

    /**
     * whether the current state is at the one supplied.
     *
     * @param runState
     * @return
     */
    fun isState(runState: RunState): Boolean = state() == runState

    /**
     * moves this state to the one supplied
     * @param state
     * @return
     */
    fun moveToState(state: RunState): RunStatus
}
