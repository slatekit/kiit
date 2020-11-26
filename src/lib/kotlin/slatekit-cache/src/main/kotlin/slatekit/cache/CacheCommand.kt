package slatekit.cache

import kotlinx.coroutines.CompletableDeferred
import slatekit.results.Outcome

/**
 * Cache commands are sent via channels to update/manage shared state ( cache values )
 * @see
 * 1. https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html
 * 2. https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html#actors
 *
 */
sealed class CacheCommand {
    abstract val action: CacheAction

    class Put(val key: String, val desc: String, val expiryInSeconds: Int, val fetcher: suspend () -> Any?) : CacheCommand() {
        override val action = CacheAction.Create
    }

    class Get(val key: String, val response: CompletableDeferred<Any?>, val load:Boolean = false) : CacheCommand() {
        override val action = CacheAction.Fetch
    }

    class GetFresh(val key: String, val response: CompletableDeferred<Any?>) : CacheCommand() {
        override val action = CacheAction.Fetch
    }

    class Set(val key: String, val value: Any?) : CacheCommand() {
        override val action = CacheAction.Update
    }

    class Refresh(val key: String, val response: CompletableDeferred<Outcome<Boolean>>) : CacheCommand() {
        override val action = CacheAction.Refresh
    }

    class Expire(val key: String, val response: CompletableDeferred<Outcome<Boolean>>) : CacheCommand() {
        override val action = CacheAction.Expire
    }

    class ExpireAll(val response: CompletableDeferred<Outcome<Boolean>>) : CacheCommand() {
        override val action = CacheAction.ExpireAll
    }

    class Delete(val key: String, val response: CompletableDeferred<Outcome<Boolean>>) : CacheCommand() {
        override val action = CacheAction.Delete
    }

    class DeleteAll(val response: CompletableDeferred<Outcome<Boolean>>) : CacheCommand() {
        override val action = CacheAction.DeleteAll
    }

    class SimpleStats(val response: CompletableDeferred<Pair<Int, List<String>>>) : CacheCommand() {
        override val action = CacheAction.Stats
    }

    class CompleteStats(val response: CompletableDeferred<List<CacheStats>>) : CacheCommand() {
        override val action = CacheAction.Stats
    }
}
