package kiit.tasks

/**
 * Convenience interface to support checking the status of an action/worker
 */
interface Checks {
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
    fun isInActive(): Boolean = isState(Status.InActive)

    /**
     * whether this is scheduled to run
     *
     * @return
     */
    fun isScheduled(): Boolean = isState(Status.Scheduled)

    /**
     * whether this is waiting
     *
     * @return
     */
    fun isReady(): Boolean = isState(Status.Ready)

    /**
     * whether this is executing
     *
     * @return
     */
    fun isRunning(): Boolean = isState(Status.Running)

    /**
     * whether this is temporarily paused
     *
     * @return
     */
    fun isPaused(): Boolean = isState(Status.Paused)

    /**
     * whether this is permanently stopped
     *
     * @return
     */
    fun isStopped(): Boolean = isState(Status.Stopped)

    /**
     * whether this is stopped
     *
     * @return
     */
    fun isFailed(): Boolean = isState(Status.Failed)

    /**
     * whether this is complete
     *
     * @return
     */
    fun isCompleted(): Boolean = isState(Status.Completed)

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
}