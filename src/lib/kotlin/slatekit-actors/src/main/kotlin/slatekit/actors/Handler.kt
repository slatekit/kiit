package slatekit.actors

interface Handler<T> {
    suspend fun handle(item:Message<T>)
}
