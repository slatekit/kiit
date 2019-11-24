package slatekit.core.slatekit.cache

import slatekit.cache.Cache
import slatekit.cache.CacheItem
import slatekit.cache.CacheSettings

class SyncCache(val cache: Cache) : Cache {
    override val settings: CacheSettings = cache.settings

    @Synchronized
    override fun size(): Int = cache.size()

    @Synchronized
    override fun keys(): List<String> = cache.keys()

    @Synchronized
    override fun contains(key: String): Boolean = cache.contains(key)

    @Synchronized
    override fun getEntry(key: String): CacheItem? = cache.getEntry(key)

    @Synchronized
    override suspend fun <T> get(key: String): T? = cache.get(key)

    @Synchronized
    override fun <T> getOrLoad(key: String): T? = cache.getOrLoad(key)

    @Synchronized
    override suspend fun <T> getFresh(key: String): T? = cache.getFresh(key)

    @Synchronized
    override suspend fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?) = cache.put(key, desc, seconds, fetcher)

    @Synchronized
    override fun <T> set(key: String, seconds: Int, value: T?) = cache.set(key, seconds, value)

    @Synchronized
    override fun remove(key: String): Boolean = cache.remove(key)

    @Synchronized
    override fun clear(): Boolean = cache.clear()

    @Synchronized
    override suspend fun refresh(key: String) = cache.refresh(key)

    @Synchronized
    override fun invalidate(key: String) = cache.invalidate(key)

    @Synchronized
    override fun invalidateAll() = cache.invalidateAll()
}
