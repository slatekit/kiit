package slatekit.cache

import kotlinx.coroutines.CompletableDeferred

sealed class CacheCommand {
    abstract val action: CacheAction

    object DeleteAll : CacheCommand() {
        override val action = CacheAction.DeleteAll
    }

    object ExpireAll : CacheCommand() {
        override val action = CacheAction.ExpireAll
    }

    class Delete(val key: String) : CacheCommand() {
        override val action = CacheAction.Delete
    }

    class Expire(val key: String) : CacheCommand() {
        override val action = CacheAction.Expire
    }

    class Refresh(val key: String) : CacheCommand() {
        override val action = CacheAction.Refresh
    }

    class Put(val key: String, val desc: String, val expiryInSeconds: Int, val fetcher: suspend () -> Any?) : CacheCommand() {
        override val action = CacheAction.Create
    }

    class Set(val key: String, val value: Any?) : CacheCommand() {
        override val action = CacheAction.Update
    }

    class GetFresh(val key: String, val deferred: CompletableDeferred<Any?>) : CacheCommand() {
        override val action = CacheAction.Fetch
    }

    class Get(val key: String, val deferred: CompletableDeferred<Any?>, val load:Boolean = false) : CacheCommand() {
        override val action = CacheAction.Fetch
    }
}
