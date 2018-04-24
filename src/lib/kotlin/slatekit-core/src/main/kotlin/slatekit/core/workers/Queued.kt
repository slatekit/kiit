package slatekit.core.workers

import slatekit.common.Result
import slatekit.common.ResultMsg
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success

/**
 * Provides support for worker that does work off of a queue
 */
interface Queued<T> {

    fun worker(): Worker<T>


    fun queue():QueueSource


    /**
     * Processes the queue
     */
    fun processQueue(): ResultMsg<T> {

        // Get items from queue
        val items = queue().nextBatchAs<T>(worker().settings.batchSize)

        return items?.let { all ->
            processItems(all)
            success(all.last())
        } ?: failure("No Items in queue")
    }


    /**
     * iterates through the batch of items and processes each one, and completes it
     * if successful, or abandons it on failure.
     *
     * @param items
     */
    fun processItems(items: List<T>?): Unit {

        items?.let { all ->

            all.forEach { item ->
                try {
                    processItem(item)
                    queue().complete(item)
                }
                catch(ex: Exception) {
                    queue().abandon(item)
                }
            }
        }
    }


    /**
     * processes a single item. derived classes should implement this.
     *
     * @param item
     */
    fun <T> processItem(item: T): Unit {
    }
}