package slatekit.integration.workers

import slatekit.common.Failure
import slatekit.common.ResultMsg
import slatekit.common.log.Logger
import slatekit.common.queues.QueueSourceMsg
import slatekit.common.results.ResultFuncs.success
import slatekit.core.workers.WorkFunction
import slatekit.core.workers.WorkNotification
import slatekit.core.workers.Worker
import slatekit.core.workers.WorkerSettings

open class WorkerWithQueues(val queues: List<QueueSourceMsg>,
                            val logger:Logger,
                            notifier:WorkNotification? = null,
                            callback: WorkFunction<Any>? = null,
                            settings: WorkerSettings)
    : Worker<Any>(notifier = notifier, callback = callback, settings = settings) {

    private val rand = java.util.Random()


    /**
     * Processes a single item from a random queue.
     */
    override fun process(args:Array<Any>?): ResultMsg<Any> {
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
    fun processItem(queue: QueueSourceMsg, message:Any?)  {

        val result = message?.let { msg ->
            try {
                processMessage(queue, message)
            } catch (ex: Exception) {
                queue.abandon(msg)
                logger.error("Error handling message from queue: " + ex.message, ex)
                Failure(ex.message ?: "", msg= "Error handling message from queue: " + ex.message)
            }
        } ?: Failure("Message not supplied")
        _lastResult.set(result)
    }


    open fun processMessage(queue:QueueSourceMsg, message:Any): ResultMsg<Any> {
        return success("not implemented")
    }
}