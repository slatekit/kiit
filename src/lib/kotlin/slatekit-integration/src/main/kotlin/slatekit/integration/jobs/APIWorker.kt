package slatekit.integration.jobs

import slatekit.apis.ApiConstants
import slatekit.apis.ApiHost
import slatekit.apis.core.Requests
import slatekit.common.Identity
import slatekit.jobs.*
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

open class APIWorker(
        val container: ApiHost,
        val settings: WorkerSettings,
        identity: Identity
)
    : Worker<Any>( identity ) {

    /**
     * Process an message from the queue represented as an API request.
     * This converts the json message body to the Request and delegates the call
     * to the container which will call the corresponding API method.
     */
    override suspend fun work(task: Task): WorkResult {

        // content ( json body )
        val rawBody = task.data

        // Convert json to request format.
        val req = Requests.fromJson(rawBody, ApiConstants.SourceQueue, ApiConstants.SourceQueue, null, task, container.ctx.enc)

        // let the container execute the request
        // this will follow the same pipeline/flow as the http requests now.
        val result = container.callAsResult(req)
        val resultFinal:Try<Any> = when(result) {
            is Success -> slatekit.results.Success(result.value)
            is Failure -> slatekit.results.Failure(result.error)
        }
        slatekit.common.TODO.IMPLEMENT("jobs", "Success/Failure handling")
        return WorkResult(WorkState.More)
    }
}