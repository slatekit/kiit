package slatekit.actors

/**
 * Message that is handled by Manager
 */
sealed class Message<T>: Envelope<T> {
    class Control<T>(val action: Action, val msg:String?, val target:String = SELF, override val sender:Any, override val data: T? = null ) : Message<T>()
    class Content<T>(override val data: T, val target:String = SELF, override val sender:Any) : Message<T>()

    companion object {
        const val SELF = "self"
    }
}


