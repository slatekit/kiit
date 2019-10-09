package slatekit.jobs

import slatekit.common.Status
import slatekit.common.ids.Identity

interface Manager {
    val job: Job.Managed
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
     * Listens to incoming requests ( name of worker )
     */
    suspend fun manage()

    /**
     * logs/handle error state/condition
     */
    suspend fun error(currentStatus:Status, message:String)
}