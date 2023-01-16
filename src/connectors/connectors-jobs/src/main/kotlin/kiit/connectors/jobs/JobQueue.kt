package kiit.connectors.jobs

import slatekit.common.Identity
import kiit.core.queues.*
import kiit.jobs.Priority
import kiit.jobs.Queue
import kiit.jobs.Task
import slatekit.results.Try

/**
 * Wraps the underlying queue holding the messages with other metadata ( name, priority )
 * The metadata can be extended in the future
 *
 * @param name : Name of the queue ( e.g. "notifications" )
 * @param priority : Priority of the queue ( low, medium, high ) for weighted selection
 * @param queue : The actual queue source / implementation
 */
data class JobQueue(
    override val name: String,
    override val priority: Priority,
    private val queue: AsyncQueue<String>,
    val id: Identity = Identity.empty) : Queue {

    /**
     * Sends an payload / task into the queue
     */
    override suspend fun send(value: String, attributes: Map<String, Any>?): Try<String> {
        return queue.send(value, attributes)
    }

    /**
     * Gets the next available task from the queue
     */
    override suspend fun next(): Task? {
        return next(this.id)
    }

    /**
     * Gets the next available task from the queue
     */
    override suspend fun next(id: Identity): Task? {
        val entry = queue.next()
        return entry?.let { task(id, entry, this) }
    }

    /**
     * Completes the task, removing it from the queue
     */
    override suspend fun done(task: Task) {
        val entry = task.entry
        if(task.entry is QueueEntry<*>) {
            queue.done(entry as QueueEntry<String>)
        }
    }

    /**
     * Fails the task, abandoning it from the queue.
     * You should implement this yourself to handle invalid/bad tasks.
     * This could be accomplished by moving them to a "dead-letter queue" or marking the tasks somehow.
     */
    override suspend fun fail(task: Task) {
        val entry = task.entry
        if(task.entry is QueueEntry<*>) {
            queue.abandon(entry as QueueEntry<String>)
        }
    }

    companion object {

        /**
         * Converts a message from any queue into a Task
         */
        fun task(identity: Identity, entry: QueueEntry<String>, queue: Queue): Task {
            val id  = entry.getTag("id") ?: ""
            val name= entry.getTag("name") ?: ""
            val data= entry.getValue()?.toString() ?: ""
            val xid = entry.getTag("xid") ?: ""
            val tag = entry.getTag("tag") ?: ""
            val task = Task(id, queue.name, identity.id, name, data, xid, tag, entry, queue)
            return task
        }
    }
}
