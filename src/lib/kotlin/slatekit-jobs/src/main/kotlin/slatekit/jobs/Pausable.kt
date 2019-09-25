package slatekit.jobs

interface Pausable {

    /**
     * Hook for handling pausing of a job
     * @param reason
     * @return
     */
    fun pause(reason:String?)


    /**
     * Hook for handling stopping of a job
     */
    fun stop(reason:String?)


    /**
     * Hook for handling resuming of a job
     * @param reason
     * @return
     */
    fun resume(reason:String?): WorkState
}