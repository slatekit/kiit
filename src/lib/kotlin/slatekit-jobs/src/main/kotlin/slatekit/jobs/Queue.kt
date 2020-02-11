package slatekit.jobs

import slatekit.common.Identity
import slatekit.core.queues.*
import slatekit.results.Try

/**
 * Wraps the underlying queue holding the messages with other metadata ( name, priority )
 * The metadata can be extended in the future
 *
 * @param name : Name of the queue ( e.g. "notifications" )
 * @param priority : Priority of the queue ( low, medium, high ) for weighted selection
 * @param queue : The actual queue source / implementation
 */
data class Queue(val name: String,
                 val priority: Priority,
                 private val queue: AsyncQueue<String>, val id: Identity = Identity.empty) {

    /**
     * Sends an payload / task into the queue
     */
    suspend fun send(value: String, attributes: Map<String, Any>? = null): Try<String> {
        return queue.send(value, attributes)
    }

    /**
     * Gets the next available task from the queue
     */
    suspend fun next():Task? {
        val entry = queue.next()
        return entry?.let { Task(id, entry, this) }
    }

    /**
     * Completes the task, removing it from the queue
     */
    suspend fun done(task:Task){
        val entry = task.entry
        queue.done(entry)
    }

    /**
     * Fails the task, abandoning it from the queue.
     * You should implement this yourself to handle invalid/bad tasks.
     * This could be accomplished by moving them to a "dead-letter queue" or marking the tasks somehow.
     */
    suspend fun fail(task:Task){
        val entry = task.entry
        queue.abandon(entry)
    }

    companion object {

        fun empty():Queue {
            val source = InMemoryQueue<String>("", QueueStringConverter())
            val queue = Queue("empty", Priority.Low, WrappedAsyncQueue(source))
            return queue
        }
    }
}
