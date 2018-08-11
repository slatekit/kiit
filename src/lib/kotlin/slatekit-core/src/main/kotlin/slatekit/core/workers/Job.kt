package slatekit.core.workers

/**
 * Represents a unit-of work ( a work-item that is handled by a Worker ).
 * A job is stored as a single message in queue or a record in a database.
 *
 * @param id       : The id of the job ( a UUID )
 * @param queue    : The name of the queue from which this job came from
 * @param task     : The category of the job ( to distinguish which worker can handle it )
 * @param payload  : The inputs/data of the job as a json payload
 * @param refId    : Serves as a correlation id
 * @param source   : The raw source/instance of the job from the queue ( e.g. could be the QueueSourceMsg )
 * @sample         :
 *
 *  id       = "ABC123",
 *  queue    = "users_01",
 *  task     = "users.sendWelcomeEmail",
 *  payload  = "JSON data...",
 *  source   = rawSource ( e.g. AWS SQS messages )
 */
data class Job(val id:String, val queue:String, val task:String, val payload:String, val refId:String, val source:Any) {

    companion object {

        val empty:Job = Job("empty", "empty", "empty", "empty", "empty", "empty")
    }
}
