package slatekit.jobs

import slatekit.common.Identity
import slatekit.results.Try

/**
 * Represents a simple interface for a Queue that can store Tasks which represent a unit of work.
 *
 * NOTES:
 * 1. A default implementation is available at @see[slatekit.integration.jobs.JobQueue]
 * 2. Default implementation uses AWS SQS by leveraging Slate Kit Cloud project @see[slatekit.cloud]
 * 3. Default implementation is NOT provided here to avoid dependency on these other libraries in this project.
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
     * Gets the next available task from the queue
     */
    suspend fun next(id: Identity): Task?

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
