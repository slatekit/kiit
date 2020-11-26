package slatekit.cache

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import slatekit.common.ids.Paired
import slatekit.common.log.Logger
import slatekit.core.common.ChannelCoordinator
import slatekit.core.common.Coordinator
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Cache implementation using channels for managed shared state ( cache data )
 * @see
 * 1. https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html
 * 2. https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html#actors
 */
class SimpleAsyncCache(private val cache:Cache,
                       val coordinator: Coordinator<CacheCommand>) : AsyncCache {

    override val name: String get() = cache.name
    override val settings: CacheSettings = cache.settings
    override val listener: ((CacheEvent) -> Unit)? get() = cache.listener
    override val logger: Logger? get() = cache.logger

    override fun <T> getAsync(key: String): Deferred<T?> = getInternal(key, false)

    override fun <T> getOrLoadAsync(key: String): Deferred<T?> = getInternal(key, true)

    override fun <T> getFreshAsync(key: String): Deferred<T?> {
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

    override suspend fun refresh(key: String):Outcome<Boolean> {
        val response = CompletableDeferred<Outcome<Boolean>>()
        request(CacheCommand.Refresh(key, response))
        return response.await()
    }

    override suspend fun expire(key: String):Outcome<Boolean> {
        val response = CompletableDeferred<Outcome<Boolean>>()
        request(CacheCommand.Expire(key, response))
        return response.await()
    }

    override suspend fun expireAll():Outcome<Boolean> {
        val response = CompletableDeferred<Outcome<Boolean>>()
        request(CacheCommand.ExpireAll(response))
        return response.await()
    }

    override suspend fun delete(key: String) :Outcome<Boolean> {
        val response = CompletableDeferred<Outcome<Boolean>>()
        request(CacheCommand.Delete(key, response))
        return response.await()
    }

    override suspend fun deleteAll():Outcome<Boolean>  {
        val response = CompletableDeferred<Outcome<Boolean>>()
        request(CacheCommand.DeleteAll(response))
        return response.await()
    }


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


    private fun manage(cmd:CacheCommand) {
        when(cmd) {
            is CacheCommand.CompleteStats -> handle(cmd.response) { cache.refresh(cmd.key) }
            is CacheCommand.SimpleStats   -> handle(cmd.response) { cache.refresh(cmd.key) }
            is CacheCommand.Refresh       -> handle(cmd.response) { cache.refresh(cmd.key) }
            is CacheCommand.Expire        -> handle(cmd.response) { cache.expire(cmd.key) }
            is CacheCommand.ExpireAll     -> handle(cmd.response) { cache.expireAll()     }
            is CacheCommand.Delete        -> handle(cmd.response) { cache.delete(cmd.key) }
            is CacheCommand.DeleteAll     -> handle(cmd.response) { cache.deleteAll() }
            is CacheCommand.Put           -> { cache.put(cmd.key, cmd.desc, cmd.expiryInSeconds, cmd.fetcher) }
            is CacheCommand.Set           -> { cache.set(cmd.key, cmd.value) }
            is CacheCommand.Get           -> {
                val item = if(cmd.load) cache.getOrLoad<Any>(cmd.key) else cache.get<Any>(cmd.key)
                cmd.response.complete(item)
            }
            is CacheCommand.GetFresh   -> {
                val item = cache.getFresh<Any>(cmd.key)
                cmd.response.complete(item)
            }
            else -> {
                // How to handle error here?
                logger?.error("Unexpected command : ${cmd.action}")
            }
        }
    }

    private fun <T> handle(response:CompletableDeferred<Outcome<T>>, op:() -> T) {
        val result = Outcomes.of { op() }
        response.complete(result)
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
