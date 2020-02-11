package slatekit.jobs

import slatekit.results.Try

/**
 * Wraps the underlying queue holding the messages with other metadata ( name, priority )
 * The metadata can be extended in the future.
 * For an implementation using Slate Kit Queues, @see[slatekit.integration.jobs.JobQueue]
 */
interface Queue {

    /**
     * Name of the queue. e.g. "notification"
     */
    val name: String

    /**
     * Priority of the queue
     */
    val priority: Priority

    /**
     * Sends an payload / task into the queue
     */
    suspend fun send(value: String, attributes: Map<String, Any>? = null): Try<String>

    /**
     * Gets the next available task from the queue
     */
    suspend fun next(): Task?

    /**
     * Completes the task, removing it from the queue
     */
    suspend fun done(task: Task)

    /**
     * Fails the task, abandoning it from the queue.
     * You should implement this yourself to handle invalid/bad tasks.
     * This could be accomplished by moving them to a "dead-letter queue" or marking the tasks somehow.
     */
    suspend fun fail(task: Task)
}


