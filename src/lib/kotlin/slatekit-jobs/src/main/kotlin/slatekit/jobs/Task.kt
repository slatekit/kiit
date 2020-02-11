package slatekit.jobs

import slatekit.jobs.workers.WorkResult

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
 * @param queue : The raw source/instance of the job from the queue ( e.g. could be the QueueSourceMsg )
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
    val entry: Any?,
    val queue: Queue?
) {

    fun structured(): List<Pair<String, String>> {
        return listOf(
            Task::id.name to id,
            Task::from.name to from,
            Task::job.name to job,
            Task::name.name to name,
            Task::xid.name to xid,
            Task::queue.name to (queue?.name ?: "")
        )
    }

    /**
     *  Acknowledges this task with the Queue to complete it
     */
    suspend fun done() {
        this.entry?.let { this.queue?.done(this) }
    }

    /**
     * Fails
     */
    suspend fun fail() {
        this.entry?.let { this.queue?.fail(this) }
    }

    companion object {

        @JvmStatic
        val empty: Task = Task("empty", "empty", "empty", "empty", "empty", "empty", "empty", null, null)
        val owned: Task = Task("owned", "owned", "owned", "owned", "owned", "owned", "owned", null, null)

        /**
         * Converts a message from any queue into a Task
         */
        fun next(state: WorkResult.Next): Task {
            val id = owned.id
            val name = owned.name
            val task = Task(id, state.offset.toString(), owned.job, name, state.reference, "", "", owned.entry, null)
            return task
        }
    }
}
