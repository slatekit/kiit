package slatekit.cache

import slatekit.common.log.Logger


class SimpleSyncCache(private val cache: SyncCache) : SyncCache {
    override val settings: CacheSettings = cache.settings

    @Synchronized
    override fun size(): Int = cache.size()

    @Synchronized
    override fun keys(): List<String> = cache.keys()

    @Synchronized
    override fun contains(key: String): Boolean = cache.contains(key)

    @Synchronized
    override fun stats(): List<CacheStats> = cache.stats()

    @Synchronized
    override fun <T> get(key: String): T? = cache.get(key)

    @Synchronized
    override fun <T> getOrLoad(key: String): T? = cache.getOrLoad(key)

    @Synchronized
    override fun <T> getFresh(key: String): T? = cache.getFresh(key)

    @Synchronized
    override fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?) = cache.put(key, desc, seconds, fetcher)

    @Synchronized
    override fun <T> set(key: String, value: T?) = cache.set(key, value)

    @Synchronized
    override fun remove(key: String): Boolean = cache.remove(key)

    @Synchronized
    override fun clear(): Boolean = cache.clear()

    @Synchronized
    override fun refresh(key: String) = cache.refresh(key)

    @Synchronized
    override fun invalidate(key: String) = cache.invalidate(key)

    @Synchronized
    override fun invalidateAll() = cache.invalidateAll()


    companion object {

        /**
         * Convenience method to build async cache using Default channel coordinator
         */
        fun of(settings: CacheSettings? = null):SimpleSyncCache {
            val raw = SimpleCache(settings ?: CacheSettings(10))
            val syncCache = SimpleSyncCache(raw)
            return syncCache
        }
    }
}
