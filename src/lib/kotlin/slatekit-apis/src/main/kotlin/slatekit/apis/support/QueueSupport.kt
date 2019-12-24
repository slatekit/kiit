package slatekit.apis.support

import slatekit.apis.ApiRequest
import slatekit.apis.core.Requests
import slatekit.common.Source
import slatekit.common.utils.Random
import slatekit.results.Notice

interface QueueSupport {


    /**
     * This can be overridden to support custom call-modes
     */
    suspend fun enueue(request: ApiRequest): Notice<String> {
        // Convert from web request to Queued request
        val req = request.request
        val payload = Requests.toJsonAsQueued(req)
        enueue(Random.uuid(), req.path, payload, req.tag)
        return slatekit.results.Success("Request processed as queue")
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
            slatekit.results.Failure("Continue processing")
        }
    }

    /**
     * Creates a request from the parameters and api info and serializes that as json
     * and submits it to a random queue.
     * This is designed to work with slatekit.jobs
     *  @param id       = "ABC123",
     *  @param name     = "users.sendWelcomeEmail",
     *  @param data     = "JSON data...",
     *  @param xid      = "abc123"
     */
    suspend fun enueue(id: String, name: String, data: String, xid: String)
}
