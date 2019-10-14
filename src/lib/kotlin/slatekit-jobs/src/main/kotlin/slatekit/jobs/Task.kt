package slatekit.jobs

import slatekit.common.queues.QueueEntry

/**
 * Represents a unit-of work ( a work-item that is handled by a Worker ).
 * A job is stored as a single message in queue or a record in a database.
 *
 * @param id      : The id of the job ( a UUID )
 * @param from    : The origin source/queue from which this job came from
 * @param name    : The name of this task ( to distinguish which worker can handle it )
 * @param data    : The inputs/data of the job as a json payload
 * @param xid     : Serves as a correlation id
 * @param tag     : Serves as a way to label this item
 * @param source  : The raw source/instance of the job from the queue ( e.g. could be the QueueSourceMsg )
 * @sample:
 *
 *  id       = "ABC123",
 *  from     = "queue://notifications",
 *  name     = "users.sendWelcomeEmail",
 *  data     = "JSON data...",
 *  xid      = "abc123"
 *  tag      = "JIRA-1234"
 *  source   = rawSource ( e.g. AWS SQS messages )
 */
data class Task(val id: String,
                val from: String,
                val name: String,
                val data: String,
                val xid : String,
                val tag : String,
                val source: Queue) {

    companion object {

        @JvmStatic
        val empty: Task = Task("empty", "empty", "empty", "empty", "empty", "empty", Queue.empty)
        val owned: Task = Task("owned", "owned", "owned", "owned", "owned", "owned", Queue.empty)


        /**
         * Converts a message from any queue into a Task
         */
        fun next(state: WorkState.Next): Task {
            val id = owned.id
            val name = owned.name
            val task = Task(id, state.offset.toString(), name, state.reference, "", "", Queue.empty)
            return task
        }



        /**
         * Converts a message from any queue into a Task
         */
        operator fun invoke(entry: QueueEntry<String>, queue: Queue): Task {
            val id = entry.getTag("id") ?: ""
            val name = entry.getTag("name") ?: ""
            val data = entry.getValue()?.toString() ?: ""
            val xid = entry.getTag("xid") ?: ""
            val tag = entry.getTag("tag") ?: ""
            val task = Task(id, queue.name, name, data, xid, tag, queue)
            return task
        }
    }
}
