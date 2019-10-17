package slatekit.jobs

import slatekit.common.Status
import slatekit.common.Identity

interface Manager {
    val job: Job

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