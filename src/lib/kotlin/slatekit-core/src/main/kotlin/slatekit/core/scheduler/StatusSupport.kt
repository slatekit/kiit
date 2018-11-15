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

package slatekit.core.scheduler

import slatekit.core.scheduler.core.Status

interface StatusSupport {

    /**
     * gets the current status of the application
     *
     * @return
     */
    fun status(): Status

    /**
     * moves the current state to idle.
     *
     * @return
     */
    fun start(): Status = moveToState(Status.Running)

    /**
     * moves the current state to paused
     *
     * @return
     */
    fun pause(): Status = moveToState(Status.Paused)

    /**
     * moves the current state to stopped.
     *
     * @return
     */
    fun stop(): Status = moveToState(Status.Stopped)

    /**
     * moves the current state to resumed
     *
     * @return
     */
    fun resume(): Status = moveToState(Status.Running)

    /**
     * moves the current state to complete
     *
     * @return
     */
    fun complete(): Status = moveToState(Status.Complete)

    /**
     * moves the current state to failed
     *
     * @return
     */
    fun fail(): Status = moveToState(Status.Failed)

    /**
     * whether this is executing
     *
     * @return
     */
    fun isRunning(): Boolean = isState(Status.Running)

    /**
     * whether this is waiting
     *
     * @return
     */
    fun isIdle(): Boolean = isState(Status.Idle)

    /**
     * whether this is paused
     *
     * @return
     */
    fun isPaused(): Boolean = isState(Status.Paused)

    /**
     * whether this is stopped
     *
     * @return
     */
    fun isStopped(): Boolean = isState(Status.Stopped)

    /**
     * whether this is complete
     *
     * @return
     */
    fun isComplete(): Boolean = isState(Status.Complete)

    /**
     * whether this has failed
     *
     * @return
     */
    fun isFailed(): Boolean = isState(Status.Failed)

    /**
     * whether this is not running ( stopped or paused )
     *
     * @return
     */
    fun isStoppedOrPaused(): Boolean = isState(Status.Stopped) || isState(Status.Paused)

    /**
     * whether the current state is at the one supplied.
     *
     * @param runState
     * @return
     */
    fun isState(status: Status): Boolean = status() == status

    /**
     * moves this state to the one supplied
     * @param state
     * @return
     */
    fun moveToState(state: Status): Status
}
