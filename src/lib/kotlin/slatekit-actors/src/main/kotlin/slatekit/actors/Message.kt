package slatekit.actors

/**
 * Message that is handled by Manager
 */
sealed class Message<T> {
    companion object {
        const val SELF = "self"
    }
}
class Control<T>(val action: Action, val msg:String? = null, val seconds:Long? = 30, val target:String = SELF) : Message<T>()
class Content<T>(val data: T, val action:Action = Action.Process, val target:String = SELF) : Message<T>()
class Request<T>(val action:Action = Action.Request, val target:String = SELF) : Message<T>()




