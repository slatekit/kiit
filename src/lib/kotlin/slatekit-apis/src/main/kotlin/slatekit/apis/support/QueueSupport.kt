package slatekit.apis.support

import slatekit.apis.ApiRequest
import slatekit.apis.core.Requests
import slatekit.common.*
import slatekit.common.queues.QueueSource
import slatekit.common.requests.Source
import slatekit.results.Notice

interface QueueSupport {

    fun queues(): List<QueueSource<String>>

    /**
     * This can be overridden to support custom call-modes
     */
    fun enueue(request: ApiRequest): Notice<String> {
        // Convert from web request to Queued request
        val req = request.request
        val payload = Requests.toJsonAsQueued(req)
        enueue(payload, Random.uuid(), req.tag, req.path)
        return slatekit.results.Success("Request processed as queue")
    }

    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    fun enqueOrProcess(request: ApiRequest): Notice<String> {
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
     */
    fun enueue(payload: String, id: String, refId: String, task: String) {
        val queues = this.queues()
        val rand = java.util.Random()
        val pos = rand.nextInt(queues.size)
        val queue = queues[pos]
        queue.send(payload, mapOf(
            "id" to id,
            "refId" to refId,
            "task" to task
        ))
    }
}
