package slatekit.cache

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import slatekit.common.ids.Paired
import slatekit.common.log.Logger
import slatekit.core.common.ChannelCoordinator
import slatekit.core.common.Coordinator

/**
 * Write are queued (via channels )
 * Reads are optimistic/dirty ( depending on method get | getOrLoad | getFresh )
 */
class SimpleAsyncCache(private val cache:SyncCache,
                       val coordinator: Coordinator<CacheCommand>) : AsyncCache {

    override val name: String get() = cache.name
    override val settings: CacheSettings = cache.settings
    override val listener: ((CacheEvent) -> Unit)? get() = cache.listener
    override val logger: Logger? get() = cache.logger

    override fun <T> get(key: String): Deferred<T?> = getInternal(key, false)

    override fun <T> getOrLoad(key: String): Deferred<T?> = getInternal(key, true)

    override fun <T> getFresh(key: String): Deferred<T?> {
        val deferred = CompletableDeferred<Any?>()
        request(CacheCommand.GetFresh(key, deferred))
        return deferred as Deferred<T?>
    }

    override fun size(): Int = cache.size()

    override fun keys(): List<String> = cache.keys()

    override fun contains(key: String): Boolean = cache.contains(key)

    override fun stats(): List<CacheStats> = cache.stats()

    override fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?) = request(CacheCommand.Put(key, "", seconds, fetcher))

    override fun <T> set(key: String, value: T?) = request(CacheCommand.Set(key, value))

    override fun remove(key: String): Boolean { request(CacheCommand.Del(key)); return true; }

    override fun clear(): Boolean  { request(CacheCommand.ClearAll); return true }

    override fun refresh(key: String) = request(CacheCommand.Refresh(key))

    override fun invalidate(key: String) = request(CacheCommand.Invalidate(key))

    override fun invalidateAll() = keys().forEach { invalidate(it) }


    fun request(command: CacheCommand) {
        coordinator.sendSync(command)
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    suspend fun manage() {
        coordinator.consume { request ->
            manage(request)
        }
    }

    /**
     * Listens to and handles 1 single request
     */
    suspend fun respond() {
        // Coordinator takes 1 request off the channel
        val request = coordinator.poll()
        request?.let {
            runBlocking {
                manage(request)
            }
        }
    }



    private fun <T> getInternal(key: String, load:Boolean): Deferred<T?> {
        val deferred = CompletableDeferred<Any?>()
        request(CacheCommand.Get(key, deferred, load))
        return deferred as Deferred<T?>
    }


    private suspend fun manage(cmd:CacheCommand) {
        when(cmd) {
            is CacheCommand.ClearAll   -> { cache.clear() }
            is CacheCommand.Clear      -> { cache.remove(cmd.key) }
            is CacheCommand.Del        -> { cache.remove(cmd.key) }
            is CacheCommand.Refresh    -> { cache.refresh(cmd.key) }
            is CacheCommand.Put        -> { cache.put(cmd.key, cmd.desc, cmd.expiryInSeconds, cmd.fetcher) }
            is CacheCommand.Set        -> { cache.set(cmd.key, cmd.value) }
            is CacheCommand.Get        -> {
                val item = if(cmd.load) cache.getOrLoad<Any>(cmd.key) else cache.get<Any>(cmd.key)
                cmd.deferred.complete(item)
            }
            is CacheCommand.GetFresh   -> {
                val item = cache.getFresh<Any>(cmd.key)
                cmd.deferred.complete(item)
            }
            is CacheCommand.Invalidate -> { cache.invalidate(cmd.key) }
            else -> {
                // How to handle error here?
                logger?.error("Unexpected command : ${cmd.action}")
            }
        }
    }


    companion object {

        /**
         * Convenience method to build async cache using Default channel coordinator
         */
        fun of(name:String, logger: Logger, settings: CacheSettings? = null, listener:((CacheEvent) -> Unit)? = null):SimpleAsyncCache {
            val raw = SimpleCache(name,settings ?: CacheSettings(10), listener)
            val coordinator = ChannelCoordinator(logger, Paired(), Channel<CacheCommand>(Channel.UNLIMITED))
            val asyncCache = SimpleAsyncCache(raw, coordinator)
            return asyncCache
        }
    }
}
