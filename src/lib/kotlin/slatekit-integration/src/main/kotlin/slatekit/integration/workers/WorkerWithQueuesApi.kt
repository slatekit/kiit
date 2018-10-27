package slatekit.integration.workers

import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.core.Requests
import slatekit.common.*
import slatekit.core.workers.*

open class WorkerWithQueuesApi(
    val container: ApiContainer,
    settings: WorkerSettings
)
    : Worker<Any>("api.queues", "apis", "apiqueues", "1.0") {

    /**
     * Process an message from the queue represented as an API request.
     * This converts the json message body to the Request and delegates the call
     * to the container which will call the corresponding API method.
     */
    override fun perform(job: Job): ResultEx<Any> {

        // content ( json body )
        val rawBody = job.payload

        // Convert json to request format.
        val req = Requests.fromJson(rawBody, ApiConstants.SourceQueue, ApiConstants.SourceQueue, null, job, container.ctx.enc)

        // let the container execute the request
        // this will follow the same pipeline/flow as the http requests now.
        val result = container.callAsResult(req)

        return result
    }
}
