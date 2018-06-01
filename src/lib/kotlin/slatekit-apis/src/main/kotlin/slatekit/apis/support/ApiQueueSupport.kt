package slatekit.apis.support

import slatekit.apis.ApiConstants
import slatekit.apis.core.Action
import slatekit.apis.core.Requests
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.ResultMsg
import slatekit.common.Success
import slatekit.common.queues.QueueSource

interface ApiQueueSupport {

    fun queues(): List<QueueSource>


    /**
     * Creates a request from the parameters and api info and serializes that as json
     * and submits it to a random queue.
     */
    fun sendToQueue(req: String) {
        val queues = this.queues()
        val rand = java.util.Random()
        val pos = rand.nextInt(queues.size)
        val queue = queues[pos]
        queue.send(req)
    }


    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    fun sendToQueueOrProcess(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?) : ResultMsg<String>  {
        // Coming in as http request ? and mode is queued ?
        return if(req.source != ApiConstants.SourceQueue && target.tag == "queued"){
            // Convert from web request to Queued request
            val queuedReq = Requests.toJsonAsQueued(req)
            sendToQueue(queuedReq)
            Success("Request processed as queue", Requests.codeHandlerProcessed)
        }
        else {
            Success("Continue processing" , Requests.codeHandlerNotProcessed)
        }
    }
}
