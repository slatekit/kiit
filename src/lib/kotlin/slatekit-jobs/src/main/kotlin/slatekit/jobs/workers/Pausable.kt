package slatekit.jobs.workers

import slatekit.jobs.Task

interface Pausable {
    /**
     * Interface for a Job that can be gracefully paused and resuming.
     * This is possible under various scenarios:
     * 1. job processes tasks from a queue, in which case, when paused, it just resumes by getting the next task
     * 2. job processes paged resources, when its on Page 20, and then paused, it can resume at Page 21
     */

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
