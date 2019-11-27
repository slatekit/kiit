package slatekit.cache

import kotlinx.coroutines.CompletableDeferred

sealed class CacheCommand {
    abstract val action: CacheAction

    object ClearAll : CacheCommand() {
        override val action = CacheAction.Clear
    }

    class Clear(val key:String) : CacheCommand() {
        override val action = CacheAction.Clear
    }

    class Check(val key: String) : CacheCommand() {
        override val action = CacheAction.Check
    }

    class Del(val key: String) : CacheCommand() {
        override val action = CacheAction.Delete
    }

    class Refresh(val key: String) : CacheCommand() {
        override val action = CacheAction.Refresh
    }

    class Invalidate(val key: String) : CacheCommand() {
        override val action = CacheAction.Invalid
    }

    class Put(val key: String, val desc: String, val expiryInSeconds: Int, val fetcher: suspend () -> Any?) : CacheCommand() {
        override val action = CacheAction.Create
    }

    class Set(val key: String, val value: Any?) : CacheCommand() {
        override val action = CacheAction.Update
    }

    class Get(val key: String, val deferred: CompletableDeferred<Any?>) : CacheCommand() {
        override val action = CacheAction.Fetch
    }
}
