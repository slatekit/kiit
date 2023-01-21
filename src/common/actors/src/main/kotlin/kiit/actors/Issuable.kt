package kiit.actors

/**
 * Provides an interface to have an actor handle messages directly
 * without being sent through its channel.
 */
interface Issuable<T> {
    suspend fun issue(item: Message<T>)
}
