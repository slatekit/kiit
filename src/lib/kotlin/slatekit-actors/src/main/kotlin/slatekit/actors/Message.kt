package slatekit.actors

/**
 * Represents the "envelope" / container for all items sent to the actor.
 */
sealed class Message<T> {
    abstract val id:Long
    abstract val reference:String

    fun print() {
        when(this) {
            is Control -> println("control: id=${this.id}, action=${this.action.name}, reference=${this.reference}, msg=${this.msg}")
            is Content -> println("content: id=${this.id}, reference=${this.reference}")
            is Request -> println("request: id=${this.id}, reference=${this.reference}")
        }
    }

    companion object {

        // Used for the reference
        const val NONE = "NONE"
    }
}


/**
 * Used to control the status of the actor ( to start, pause, resume, stop )
 */
class Control<T>(override val id:Long, val action: Action, val msg: String? = null, val seconds: Long? = 30, override val reference: String = NONE) : Message<T>()


/**
 * Used for actual payloads to send data to the actor for processing
 */
class Content<T>(override val id:Long, val data: T, override val reference: String = NONE) : Message<T>()


/**
 * Used for actors that can load payloads them selves
 * NOTE: This is for usage in slatekit.jobs
 */
class Request<T>(override val id:Long, override val reference: String = NONE) : Message<T>()




