package slatekit.integration.workers

import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.core.Reqs
import slatekit.common.Result
import slatekit.common.log.Logger
import slatekit.common.queues.QueueSourceMsg
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.core.workers.*


open class WorkerWithQueuesApi(val container: ApiContainer,
                          queues: List<QueueSourceMsg>,
                          notifier:WorkNotification? = null,
                          callback: WorkFunction<Any>? = null,
                          settings: WorkerSettings)
                    : WorkerWithQueues(queues, container.ctx.log,
        notifier, callback, settings) {

    /**
     * Process an message from the queue represented as an API request.
     * This converts the json message body to the Request and delegates the call
     * to the container which will call the corresponding API method.
     */
    override fun processMessage(queue: QueueSourceMsg, msg:Any) : Result<Any> {

        // content ( json body )
        val rawBody = queue.getMessageBody(msg)

        // Convert json to request format.
        val req = Reqs.fromJson(null, msg, ApiConstants.SourceQueue, rawBody, container.ctx.enc)

        // let the container execute the request
        // this will follow the same pipeline/flow as the http requests now.
        val result = container.callAsResult(req)

        return if(result.success) {
            // acknowledge/complete message
            queue.complete(msg)

            success<Any>(result, req.fullName)
        }
        else {
            queue.abandon(msg)
            result
        }
    }
}
