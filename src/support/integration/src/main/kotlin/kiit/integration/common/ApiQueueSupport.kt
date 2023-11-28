package kiit.integration.common

import kiit.apis.ApiRequest
import kiit.apis.ApiResult
import kiit.apis.Middleware
import kiit.apis.core.Reqs
import kiit.apis.support.QueueSupport
import kiit.common.Source
import kiit.common.utils.Random
import kiit.core.queues.AsyncQueue
import kiit.results.Outcome
import kiit.results.Success

interface ApiQueueSupport : QueueSupport, Middleware{


    fun queues(): List<AsyncQueue<String>>

    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    override suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult>  {
        // Coming in as http request ? and mode is queued ?
        val isQueued = true //&& req.target?.action?.tags?.contains("queued") == true
        return if(req.source != Source.Queue && isQueued){
//            // Convert from web request to Queued request
            val queuedReq = Reqs.toJsonAsQueued(req.request)
            enueue(Random.guid(), req.request.fullName, queuedReq,  "api-queue")
            Success("Request processed as queue")
        }
        else {
            next(req)
        }
    }


    /**
     * Creates a request from the parameters and api info and serializes that as json
     * and submits it to a random queue.
     * This is designed to work with kiit.jobs
     *  @param id       = "ABC123",
     *  @param name     = "users.sendWelcomeEmail",
     *  @param data     = "JSON data...",
     *  @param xid      = "abc123"
     */
    override suspend fun enueue(id: String, name: String, data: String, xid: String) {
        val queues = this.queues()
        val rand = java.util.Random()
        val pos = rand.nextInt(queues.size)
        val queue = queues[pos]
        queue.send(data, mapOf(
                "id" to id,
                "name" to name,
                "xid" to xid
        ))
    }
}