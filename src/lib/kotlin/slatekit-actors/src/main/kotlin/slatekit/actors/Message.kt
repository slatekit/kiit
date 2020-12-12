package slatekit.actors

/**
 * Represents the "envelope" / container for all items sent to the actor.
 */
sealed class Message<T> {
    abstract val id:Long
    abstract val target:String
    companion object {
        const val SELF = "self"
    }
}


/**
 * Used to control the status of the actor ( to start, pause, resume, stop )
 */
class Control<T>(override val id:Long, val action: Action, val msg: String? = null, val seconds: Long? = 30, override val target: String = SELF) : Message<T>()


/**
 * Used for actual payloads to send data to the actor for processing
 */
class Content<T>(override val id:Long, val data: T, override val target: String = SELF) : Message<T>()


/**
 * Used for actors that can load payloads them selves
 * NOTE: This is for usage in slatekit.jobs
 */
class Request<T>(override val id:Long, val action: Action = Action.Request, override val target: String = SELF) : Message<T>()




