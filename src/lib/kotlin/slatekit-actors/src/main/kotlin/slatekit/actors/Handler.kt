package slatekit.actors

/**
 * Provides an interface to have an actor handle messages directly
 * without being sent through its channel.
 */
interface Handler<T> {
    suspend fun handle(item:Message<T>)
}
