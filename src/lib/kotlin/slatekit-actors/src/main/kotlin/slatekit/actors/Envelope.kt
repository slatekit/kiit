package slatekit.actors

interface Envelope<T> {
    val sender:Any
    val data:T?
}
