package slatekit.jobs

interface Pausable {

    /**
     * Hook for handling pausing of a job
     * @param reason
     * @return
     */
    suspend fun pause(reason: String?)

    /**
     * Hook for handling stopping of a job
     */
    suspend fun stop(reason: String?)

    /**
     * Hook for handling resuming of a job
     * @param reason
     * @return
     */
    suspend fun resume(reason: String?, task: Task): WorkResult
}
