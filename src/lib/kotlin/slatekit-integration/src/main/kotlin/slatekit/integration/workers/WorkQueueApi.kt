package slatekit.integration.workers

import slatekit.common.results.ResultFuncs
import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.core.Reqs
import slatekit.common.Result
import slatekit.common.queues.QueueSourceMsg
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.core.workers.*


class WorkerQueueApi( val container: ApiContainer,
                      val queues: List<QueueSourceMsg>,
                      notifier:WorkNotification? = null,
                      callback: WorkFunction<Any>? = null,
                      settings: WorkerSettings)
                    : Worker<Any>(notifier = notifier, callback = callback, settings = settings) {

    private val rand = java.util.Random()


    /**
     * Processes a single item from a random queue.
     */
    override fun process(args:Array<Any>?): Result<Any> {
        val ndx = rand.nextInt(queues.size)
        val queue = queues[ndx]
        processItem(queue, queue.next())
        return _lastResult.get()
    }


    /**
     * Process an message from the queue represented as an API request.
     * This converts the json message body to the Request and delegates the call
     * to the container which will call the corresponding API method.
     */
    fun processItem(queue: QueueSourceMsg, message:Any?) : Unit {

        val result = message?.let { msg ->
            try {
                // content ( json body )
                val rawBody = queue.getMessageBody(msg)

                // Convert json to request format.
                val req = Reqs.fromJson(msg, ApiConstants.SourceQueue, rawBody, container.ctx.enc)

                // let the container execute the request
                // this will follow the same pipeline/flow as the http requests now.
                val result = container.call(req)

                if(result.success) {
                    // acknowledge/complete message
                    queue.complete(msg)

                    success<Any>(result, req.fullName, req.tag)
                }
                else {
                    queue.abandon(msg)
                    result
                }

            } catch (ex: Exception) {

                // handle bad msg
                queue.abandon(msg)

                failure<Any>("Error handling message from queue: " + ex.message, ex)
            }
        } ?: failure<Any>("Message not supplied")
        _lastResult.set(result)
    }
}