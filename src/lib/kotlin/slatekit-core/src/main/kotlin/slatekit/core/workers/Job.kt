package slatekit.core.workers

/**
 * Represents a unit-of work ( a work-item that is handled by a Worker )
 * @param id       : The id of the job ( a UUID )
 * @param queue    : The name of the queue from which this job came from
 * @param category : The category of the job ( to distinguish which worker can handle it )
 * @param payload  : The inputs/data of the job as a json payload
 * @param source   : The raw source/instance of the job from the queue ( e.g. could be the QueueSourceMsg )
 */
data class Job(val id:String, val queue:String, val category:String, val payload:String, val source:Any)