package slatekit.core.slatekit.core.actors

sealed class Message<T> {
    class Control<T>(val action: Action, val msg:String?) : Message<T>()
    class Perform<T>(val data: T) : Message<T>()
}