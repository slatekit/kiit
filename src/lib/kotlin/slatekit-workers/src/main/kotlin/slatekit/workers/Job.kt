package slatekit.workers

import slatekit.common.queues.QueueEntry

/**
 * Represents a unit-of work ( a work-item that is handled by a Worker ).
 * A job is stored as a single message in queue or a record in a database.
 *
 * @param id      : The id of the job ( a UUID )
 * @param queue   : The name of the queue from which this job came from
 * @param task    : The category of the job ( to distinguish which worker can handle it )
 * @param payload : The inputs/data of the job as a json payload
 * @param refId   : Serves as a correlation id
 * @param source  : The raw source/instance of the job from the queue ( e.g. could be the QueueSourceMsg )
 * @sample:
 *
 *  id       = "ABC123",
 *  queue    = "notifications",
 *  task     = "users.sendWelcomeEmail",
 *  payload  = "JSON data...",
 *  refId    = "abc123"
 *  source   = rawSource ( e.g. AWS SQS messages )
 */
data class Job(val id: String,
               val queue: String,
               val task: String,
               val payload: String,
               val refId: String,
               val source: Any) {

    companion object {

        @JvmStatic
        val empty: Job =
                Job("empty", "empty", "empty", "empty", "empty", "empty")


        /**
         * Converts a message from any queue into a Job
         */
        operator fun invoke(entry: QueueEntry<String>, queueInfo: Queue): Job {
            val id = entry.getTag("id") ?: ""
            val refId = entry.getTag("refId") ?: ""
            val task = entry.getTag("task") ?: ""
            val body = entry.getValue()?.toString() ?: ""
            val job = Job(id, queueInfo.name, task, body, refId, entry)
            return job
        }
    }
}
