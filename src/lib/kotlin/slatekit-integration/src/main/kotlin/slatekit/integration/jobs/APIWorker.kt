package slatekit.integration.jobs

import slatekit.jobs.WResult
import slatekit.apis.ApiServer
import slatekit.apis.core.Reqs
import slatekit.common.Identity
import slatekit.common.Sources
import slatekit.jobs.*
import slatekit.jobs.Worker
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

open class APIWorker(
        val container: ApiServer,
        identity: Identity
)
    : Worker<Any>( identity ) {

    /**
     * Process an message from the queue represented as an API request.
     * This converts the json message body to the Request and delegates the call
     * to the container which will call the corresponding API method.
     */
    override suspend fun work(task: Task): WResult {

        // content ( json body )
        val rawBody = task.data

        // Convert json to request format.
        val req = Reqs.fromJson(rawBody, Sources.QUEUE, Sources.QUEUE, null, task, container.ctx.enc)

        // let the container execute the request
        // this will follow the same pipeline/flow as the http requests now.
        val result = container.execute(req)
        val resultFinal:Try<Any> = when(result) {
            is Success -> slatekit.results.Success(result.value)
            is Failure -> result.toTry()
        }
        slatekit.common.NOTE.IMPLEMENT("jobs", "Success/Failure handling")
        return WResult.More
    }
}