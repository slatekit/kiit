package slatekit.integration.workers

import slatekit.common.Result
import slatekit.common.log.LoggerBase
import slatekit.common.queues.QueueSourceMsg
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.success
import slatekit.core.workers.WorkFunction
import slatekit.core.workers.WorkNotification
import slatekit.core.workers.Worker
import slatekit.core.workers.WorkerSettings

open class WorkerWithQueues(val queues: List<QueueSourceMsg>,
                       val logger:LoggerBase,
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
                processMessage(queue, message)
            } catch (ex: Exception) {
                queue.abandon(msg)
                logger.error("Error handling message from queue: " + ex.message, ex)
                ResultFuncs.failure<Any>("Error handling message from queue: " + ex.message, ex)
            }
        } ?: ResultFuncs.failure<Any>("Message not supplied")
        _lastResult.set(result)
    }


    open fun processMessage(queue:QueueSourceMsg, message:Any): Result<Any> {
        return success("not implemented")
    }
}