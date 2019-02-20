package slatekit.apis.support

import slatekit.apis.ApiConstants
import slatekit.apis.core.Action
import slatekit.apis.core.Requests
import slatekit.common.*
import slatekit.common.queues.QueueSource
import slatekit.common.requests.Request
import slatekit.results.Notice

interface ApiQueueSupport {

    fun queues(): List<QueueSource<String>>

    /**
     * Creates a request from the parameters and api info and serializes that as json
     * and submits it to a random queue.
     */
    fun sendToQueue(payload: String, id: String, refId: String, task: String) {
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

    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    fun sendToQueueOrProcess(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?): Notice<String> {
        // Coming in as http request and mode is queued ?
        return if (req.source != ApiConstants.SourceQueue && target.tag == "queued") {
            sendToQueue(ctx, req, target, source, args)
        } else {
            slatekit.results.Failure("Continue processing")
        }
    }

    /**
     * This can be overridden to support custom call-modes
     */
    fun sendToQueue(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?): Notice<String> {
        // Convert from web request to Queued request
        val payload = Requests.toJsonAsQueued(req)
        sendToQueue(payload, Random.uuid(), req.tag, req.path)
        return slatekit.results.Success("Request processed as queue")
    }
}
