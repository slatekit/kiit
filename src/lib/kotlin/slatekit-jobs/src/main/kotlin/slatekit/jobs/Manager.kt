package slatekit.jobs

import slatekit.common.Status
import slatekit.common.Identity

interface Manager {

    /**
     * Requests starting of the job
     */
    suspend fun start() = request(JobAction.Start)

    /**
     * Requests stopping of the job
     */
    suspend fun stop() = request(JobAction.Stop)

    /**
     * Requests pausing of the job
     */
    suspend fun pause() = request(JobAction.Pause)

    /**
     * Requests resuming of the job
     */
    suspend fun resume() = request(JobAction.Resume)

    /**
     * Requests processing of the job
     */
    suspend fun process() = request(JobAction.Process)

    /**
     * Requests processing to slow down
     */
    suspend fun slow() = request(JobAction.Slow)

    /**
     * Requests processing to speed up
     */
    suspend fun fast () = request(JobAction.Fast)

    /**
     * Requests an action on the entire job
     */
    suspend fun request(action: JobAction)

    /**
     * Requests an action on a specific worker
     */
    suspend fun request(action: JobAction, workerId: Identity, desc:String?)

    /**
     * Requests an action to manage a job/worker
     */
    suspend fun request(request: JobRequest)


    /**
     * Listens to and handles 1 request
     */
    suspend fun respond()

    /**
     * Listens to and handles all incoming requests
     */
    suspend fun manage()

    /**
     * logs/handle error state/condition
     */
    suspend fun error(currentStatus:Status, message:String)

}