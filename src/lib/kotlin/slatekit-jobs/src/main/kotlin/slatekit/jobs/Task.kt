package slatekit.jobs

import slatekit.common.Identity
import slatekit.core.queues.QueueEntry

/**
 * Represents a unit-of work ( a work-item that is handled by a Worker ).
 * A job is stored as a single message in queue or a record in a database.
 *
 * @param id : The id of the job ( a UUID )
 * @param from : The origin source/queue from which this job came from
 * @param job : The job associatd with this task
 * @param name : The name of this task ( to distinguish which worker can handle it )
 * @param data : The inputs/data of the job as a json payload
 * @param xid : Serves as a correlation id
 * @param tag : Serves as a way to label this item
 * @param source : The raw source/instance of the job from the queue ( e.g. could be the QueueSourceMsg )
 * @sample:
 *
 *  id       = "ABC123",
 *  from     = "queue://notifications",
 *  job      = "job1"
 *  name     = "users.sendWelcomeEmail",
 *  data     = "JSON data...",
 *  xid      = "abc123"
 *  tag      = "JIRA-1234"
 *  source   = rawSource ( e.g. AWS SQS messages )
 */
data class Task(
    val id: String,
    val from: String,
    val job: String,
    val name: String,
    val data: String,
    val xid: String,
    val tag: String,
    val entry: QueueEntry<String>?,
    val source: Queue
) {

    /**
     *  Acknowledges this task with the Queue to complete it
     */
    fun done() {
        this.entry?.let { this.source.queue.done(it) }
    }

    /**
     * Fails
     */
    fun fail() {
        this.entry?.let { this.source.queue.abandon(it) }
    }

    companion object {

        @JvmStatic
        val empty: Task = Task("empty", "empty", "empty", "empty", "empty", "empty", "empty", null, Queue.empty)
        val owned: Task = Task("owned", "owned", "owned", "owned", "owned", "owned", "owned", null, Queue.empty)

        /**
         * Converts a message from any queue into a Task
         */
        fun next(state: WorkState.Next): Task {
            val id = owned.id
            val name = owned.name
            val task = Task(id, state.offset.toString(), owned.job, name, state.reference, "", "", owned.entry, Queue.empty)
            return task
        }

        /**
         * Converts a message from any queue into a Task
         */
        operator fun invoke(identity: Identity, entry: QueueEntry<String>, queue: Queue): Task {
            val id = entry.getTag("id") ?: ""
            val name = entry.getTag("name") ?: ""
            val data = entry.getValue()?.toString() ?: ""
            val xid = entry.getTag("xid") ?: ""
            val tag = entry.getTag("tag") ?: ""
            val task = Task(id, queue.name, identity.id, name, data, xid, tag, entry, queue)
            return task
        }
    }
}
