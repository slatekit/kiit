package slatekit.cache

import slatekit.core.common.Coordinator

/**
 * Write are queued (via channels )
 * Reads are optimistic/dirty ( depending on method get | getOrLoad | getFresh )
 */
class ChannelCache(private val cache:Cache, private val coordinator: Coordinator<CacheCommand>) : Cache {

    override val settings: CacheSettings = cache.settings

    override fun size(): Int = cache.size()

    override fun keys(): List<String> = cache.keys()

    override fun contains(key: String): Boolean = cache.contains(key)

    override fun stats(): List<CacheStats> = cache.stats()

    override fun <T> get(key: String): T? = cache.get(key)

    @Synchronized
    override fun <T> getOrLoad(key: String): T? = cache.getOrLoad(key)

    @Synchronized
    override fun <T> getFresh(key: String): T? = cache.getFresh(key)

    fun get(key:String, onReady:suspend(Any?) -> Unit ) = request(CacheCommand.Get(key, onReady))

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
            manage(request, false)
        }
    }


    private suspend fun manage(cmd:CacheCommand, launch:Boolean) {
        when(cmd) {
            is CacheCommand.ClearAll -> { cache.clear() }
            is CacheCommand.Clear    -> { cache.remove(cmd.key) }
            is CacheCommand.Del      -> { cache.remove(cmd.key) }
            is CacheCommand.Refresh  -> { cache.refresh(cmd.key) }
            is CacheCommand.Put      -> { cache.put(cmd.key, cmd.desc, cmd.expiryInSeconds, cmd.fetcher) }
            is CacheCommand.Set      -> { cache.set(cmd.key, cmd.value) }
            is CacheCommand.Get      -> { cmd.onReady(cache.get(cmd.key)) }
            else -> {
                TODO("WIP")
            }
        }
    }
}
