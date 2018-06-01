package slatekit.integration.workers

import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.core.Requests
import slatekit.common.*
import slatekit.common.queues.QueueSourceMsg
import slatekit.core.workers.*


open class WorkerWithQueuesApi(val container: ApiContainer,
                          queues: List<QueueSourceMsg>,
                          notifier:WorkNotification? = null,
                          callback: WorkFunction<Any>? = null,
                          settings: WorkerSettings)
                    : WorkerWithQueues(queues, container.ctx.logs.getLogger(),
        notifier, callback, settings) {

    /**
     * Process an message from the queue represented as an API request.
     * This converts the json message body to the Request and delegates the call
     * to the container which will call the corresponding API method.
     */
    override fun processMessage(queue: QueueSourceMsg, msg:Any) : ResultMsg<Any> {

        // content ( json body )
        val rawBody = queue.getMessageBody(msg)

        // Convert json to request format.
        val req = Requests.fromJson(rawBody, ApiConstants.SourceQueue, ApiConstants.SourceQueue, null, msg, container.ctx.enc)

        // let the container execute the request
        // this will follow the same pipeline/flow as the http requests now.
        val result = container.callAsResult(req)

        return if(result.success) {
            // acknowledge/complete message
            queue.complete(msg)

            Success(result, msg = req.fullName)
        }
        else {
            queue.abandon(msg)
            result.transform( { it -> Success(it)}, { err -> Failure(err.message ?: "") })
        }
    }
}
