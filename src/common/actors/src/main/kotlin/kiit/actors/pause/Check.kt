package kiit.actors.pause

import kiit.actors.Status

interface Check {
    /**
     * gets the current status of the application
     *
     * @return
     */
    fun status(): Status

    /**
     * whether this is started
     *
     * @return
     */
    fun isStarted(): Boolean = isState(Status.Started)

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
    fun isIdle(): Boolean = isState(Status.Waiting)

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
    fun isCompleted(): Boolean = isState(Status.Completed)

    /**
     * whether this has failed
     *
     * @return
     */
    fun isFailed(): Boolean = isState(Status.Failed)

    /**
     * whether this has been killed, there is no restart possible
     *
     * @return
     */
    fun isKilled(): Boolean = isState(Status.Killed)

    /**
     * whether this is not running ( stopped or paused )
     *
     * @return
     */
    fun isStoppedOrPaused(): Boolean = isState(Status.Stopped) || isState(Status.Paused)

    /**
     * whether this is not running ( stopped or paused or killed)
     *
     * @return
     */
    fun isStoppedOrPausedOrKilled(): Boolean = isState(Status.Stopped) || isState(Status.Paused) || isState(Status.Killed)

    /**
     * whether the current state is at the one supplied.
     *
     * @param runState
     * @return
     */
    fun isState(status: Status): Boolean = status() == status
}
