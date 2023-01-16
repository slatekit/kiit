package kiit.apis.support

import kiit.apis.ApiRequest
import kiit.apis.core.Reqs
import kiit.common.Source
import kiit.common.utils.Random
import kiit.results.Notice

interface QueueSupport {


    /**
     * This can be overridden to support custom call-modes
     */
    suspend fun enueue(request: ApiRequest): Notice<String> {
        // Convert from web request to Queued request
        val req = request.request
        val payload = Reqs.toJsonAsQueued(req)
        enueue(Random.uuid(), req.path, payload, req.tag)
        return kiit.results.Success("Request processed as queue")
    }


    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    suspend fun enqueOrProcess(request: ApiRequest): Notice<String> {
        // Coming in as http request and mode is queued ?
        val req = request.request
        return if (req.source != Source.Queue && request.target?.action?.tags?.contains("queued") == true) {
            enueue(request)
        } else {
            kiit.results.Failure("Continue processing")
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
    suspend fun enueue(id: String, name: String, data: String, xid: String)
}
