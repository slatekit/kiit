package kiit.cache

import slatekit.common.Identity
import slatekit.common.log.Logger
import slatekit.results.Outcome


class SimpleSyncCache(private val cache: Cache) : Cache {


    override val id: Identity get() = cache.id
    override val settings: CacheSettings = cache.settings
    override val listener: ((CacheEvent) -> Unit)? get() = cache.listener
    override val logger: Logger? get() = cache.logger

    @Synchronized
    override fun size(): Int = cache.size()

    @Synchronized
    override fun keys(): List<String> = cache.keys()

    @Synchronized
    override fun contains(key: String): Boolean = cache.contains(key)

    @Synchronized
    override fun stats(): List<CacheStats> = cache.stats()

    @Synchronized
    override fun stats(key:String): CacheStats? = cache.stats(key)

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
    override fun delete(key: String): Outcome<Boolean> = cache.delete(key)

    @Synchronized
    override fun deleteAll(): Outcome<Boolean> = cache.deleteAll()

    @Synchronized
    override fun refresh(key: String):Outcome<Boolean>  = cache.refresh(key)

    @Synchronized
    override fun expire(key: String):Outcome<Boolean>  = cache.expire(key)

    @Synchronized
    override fun expireAll():Outcome<Boolean> = cache.expireAll()

    companion object {

        /**
         * Convenience method to build async cache using Default channel coordinator
         */
        fun of(id:Identity, settings: CacheSettings? = null, listener:((CacheEvent) -> Unit)? = null):SimpleSyncCache {
            val raw = SimpleCache(id,settings ?: CacheSettings(10), listener )
            val syncCache = SimpleSyncCache(raw)
            return syncCache
        }
    }
}
