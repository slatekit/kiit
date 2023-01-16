package kiit.connectors.jobs

import kiit.jobs.WResult
import kiit.apis.ApiServer
import kiit.apis.core.Reqs
import kiit.common.Identity
import kiit.common.Sources
import kiit.jobs.*
import kiit.jobs.Worker

open class JobAPIWorker(
        val server: ApiServer,
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
        val req = Reqs.fromJson(rawBody, Sources.QUEUE, Sources.QUEUE, null, task, server.ctx.enc)

        // let the container execute the request
        // this will follow the same pipeline/flow as the http requests now.
        val result = server.execute(req)
        kiit.common.NOTE.IMPLEMENT("jobs", "Success/Failure handling")
        return WResult.More
    }
}