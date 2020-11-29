package slatekit.cache

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import slatekit.common.log.Logger
import slatekit.common.log.LoggerConsole
import slatekit.results.Outcome

/**
 * Cache implementation using channels for managed shared state ( cache data )
 * @see
 * 1. https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html
 * 2. https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html#actors
 * 3. https://github.com/Kotlin/kotlinx.coroutines/issues/87
 */
class SimpleAsyncCache(private val cache: Cache,
                       val channel:Channel<CacheCommand> = Channel(Channel.UNLIMITED)) : AsyncCache {

    override val name: String get() = cache.name
    override val settings: CacheSettings = cache.settings
    override val listener: ((CacheEvent) -> Unit)? get() = cache.listener
    override val logger: Logger? get() = cache.logger

    override suspend fun size(): Int {
        val res = perform<Pair<Int, List<String>>> { response -> CacheCommand.SimpleStats(response) }
        return res.first
    }

    override suspend fun keys(): List<String> {
        val res = perform<Pair<Int, List<String>>> { response -> CacheCommand.SimpleStats(response) }
        return res.second
    }

    override suspend fun contains(key: String): Boolean {
        return perform { response -> CacheCommand.Exists(key, response) }
    }

    override suspend fun stats(): List<CacheStats> {
        return perform { response -> CacheCommand.CompleteStats(response) }
    }

    override suspend fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?) {
        return request<Boolean> { response -> CacheCommand.Put(key, "", seconds, fetcher, response) }
    }

    override suspend fun <T> set(key: String, value: T?) {
        return request<Boolean> { response -> CacheCommand.Set(key, value, response) }
    }

    override suspend fun <T> getAsync(key: String): Deferred<T?> = getInternalAsync(key, false)

    override suspend fun <T> getOrLoadAsync(key: String): Deferred<T?> = getInternalAsync(key, true)

    override suspend fun <T> getFreshAsync(key: String): Deferred<T?> {
        val deferred = CompletableDeferred<Any?>()
        channel.send(CacheCommand.GetFresh(key, deferred))
        return deferred as Deferred<T?>
    }

    override suspend fun <T> get(key: String): T? {
        val res = perform<Any?> { response -> CacheCommand.Get(key, response, false) }
        return res as T?
    }

    override suspend fun <T> getOrLoad(key: String): T? {
        val res = perform<Any?> { response -> CacheCommand.Get(key, response, true) }
        return res as T?
    }

    override suspend fun <T> getFresh(key: String): T? {
        val res = perform<Any?> { response -> CacheCommand.GetFresh(key, response) }
        return res as T?
    }

    override suspend fun refresh(key: String): Outcome<Boolean> {
        return operate { response -> CacheCommand.Refresh(key, response) }
    }

    override suspend fun expire(key: String): Outcome<Boolean> {
        return operate { response -> CacheCommand.Expire(key, response) }
    }

    override suspend fun expireAll(): Outcome<Boolean> {
        return operate { response -> CacheCommand.ExpireAll(response) }
    }

    override suspend fun delete(key: String): Outcome<Boolean> {
        return operate { response -> CacheCommand.Delete(key, response) }
    }

    override suspend fun deleteAll(): Outcome<Boolean> {
        return operate { response -> CacheCommand.DeleteAll(response) }
    }

    /** =================================================================
    * START: Management methods
    * ===================================================================
    */
    /**
     * Kicks off the cache by having it listen to all the commands being sent.
     */
    suspend fun work() {
        for (cmd in channel) {
            manage(cmd)
            yield()
        }
    }

    /**
     * Sends 1 cache command to be processed.
     */
    suspend fun send(cmd: CacheCommand) {
        channel.send(cmd)
    }

    /**
     * Processes 1 cache command of the channel/queue.
     */
    suspend fun poll() {
        val item = channel.poll()
        item?.let { manage(it) }
    }

    /**
     * Stops processing all cache commands
     */
    suspend fun stop():Boolean {
        return channel.close()
    }


    private suspend fun <T> getInternalAsync(key: String, load: Boolean): Deferred<T?> {
        val deferred = CompletableDeferred<Any?>()
        channel.send(CacheCommand.Get(key, deferred, load))
        return deferred as Deferred<T?>
    }


    private fun manage(cmd: CacheCommand) {
        when (cmd) {
            /* ktlint-disable */
            is CacheCommand.Exists -> handleValued(cmd.response) { cache.contains(cmd.key) }
            is CacheCommand.CompleteStats -> handleValued(cmd.response) { cache.stats() }
            is CacheCommand.SimpleStats -> handleValued(cmd.response) { Pair(cache.size(), cache.keys()) }
            is CacheCommand.Refresh -> handleOutcome(cmd.response) { cache.refresh(cmd.key) }
            is CacheCommand.Expire -> handleOutcome(cmd.response) { cache.expire(cmd.key) }
            is CacheCommand.ExpireAll -> handleOutcome(cmd.response) { cache.expireAll() }
            is CacheCommand.Delete -> handleOutcome(cmd.response) { cache.delete(cmd.key) }
            is CacheCommand.DeleteAll -> handleOutcome(cmd.response) { cache.deleteAll() }
            /* ktlint-enable */
            is CacheCommand.Put -> {
                cache.put(cmd.key, cmd.desc, cmd.expiryInSeconds, cmd.fetcher)
            }
            is CacheCommand.Set -> {
                cache.set(cmd.key, cmd.value)
            }
            is CacheCommand.Get -> {
                val item = if (cmd.load) cache.getOrLoad<Any>(cmd.key) else cache.get<Any>(cmd.key)
                cmd.response.complete(item)
            }
            is CacheCommand.GetFresh -> {
                val item = cache.getFresh<Any>(cmd.key)
                cmd.response.complete(item)
            }
            else -> {
                // How to handle error here?
                logger?.error("Unexpected command : ${cmd.action}")
            }
        }
    }


    private suspend fun <T> operate(op: (CompletableDeferred<Outcome<T>>) -> CacheCommand): Outcome<T> {
        val response = CompletableDeferred<Outcome<T>>()
        val command = op(response)
        channel.send(command)
        return response.await()
    }


    private suspend fun <T> perform(op: (CompletableDeferred<T>) -> CacheCommand): T {
        val response = CompletableDeferred<T>()
        val command = op(response)
        channel.send(command)
        val result = response.await()
        return result
    }


    private suspend fun <T> request(op: (CompletableDeferred<T>) -> CacheCommand) {
        val response = CompletableDeferred<T>()
        val command = op(response)
        channel.send(command)
    }


    private fun <T> handleOutcome(response: CompletableDeferred<Outcome<T>>, op: () -> Outcome<T>) {
        val result = op()
        response.complete(result)
    }


    private fun <T> handleValued(response: CompletableDeferred<T>, op: () -> T) {
        val result = op()
        response.complete(result)
    }


    companion object {

        /**
         * Convenience method to build async cache using Default channel coordinator
         */
        fun of(name: String = "channel-cache", logger: Logger = LoggerConsole(), settings: CacheSettings? = null, listener: ((CacheEvent) -> Unit)? = null): SimpleAsyncCache {
            val raw = SimpleCache(name, settings = settings ?: CacheSettings(10), listener = listener, logger = logger)
            return  SimpleAsyncCache(raw)
        }
    }
}
