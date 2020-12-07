/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.cache

import slatekit.common.Identity
import slatekit.common.log.Logger
import slatekit.common.utils.Random
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.tracking.Tracker

/**
 * This light-weight implementation of a Cache ( LRU - Least recently used )
 * contains the following design approaches:
 *
 * 1. designed for low frequency writes
 * 2. Storing any type [T] item
 * 3. Storing a function, supplied by caller that can handle refreshing the cache
 * 4. allowing for invalidating a cache item
 * 5. allowing for invalidating all cache items
 * 6. designed to get or refresh an item asynchronously
 * 7. customizable expiration time per cache item
 *
 * @param settings
 */
open class SimpleCache(override val id:Identity = Identity.app("app", "cache"),
                       override val settings: CacheSettings,
                       override val listener:((CacheEvent) -> Unit)? = null,
                       override val logger: Logger? = null ) : Cache {

    /**
     * The LinkedHashMap already LRU(Least Recently Used) behaviour out of the box.
     */
    protected val lruCache = LRUMap<String, CacheEntry>(settings.size)

    /**
     * Stats to record cache accesses and cache misses.
     * CacheEntry only records hits as it only exist if there is an entry for the key
     */
    protected val accesses  = LRUMap<String, Pair<Tracker<Any>, Tracker<Any>>>(settings.size)


    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @tparam T
     * @return
     */
    override fun <T> get(key: String): T? {
        return getInternal(key, false)
    }

    /**
     * gets a cache item or loads it if not available, via a future
     *
     * @param key
     * @tparam T
     * @return
     */
    override fun <T> getOrLoad(key: String): T? {
        return getInternal(key, true)
    }

    /**
     * manual / explicit refresh of a cache item with a future result
     * in order to get the item
     *
     * @param key
     */
    override fun <T> getFresh(key: String): T? {
        refresh(key)
        return get(key)
    }

    /**
     * size of the cache
     *
     * @return
     */
    override fun size(): Int = lruCache.size

    /**
     * size of the cache
     *
     * @return
     */
    override fun keys(): List<String> = lruCache.keys.map { it }

    /**
     * whether this cache contains the entry with the supplied key
     *
     * @param key
     * @return
     */
    override fun contains(key: String): Boolean = lruCache.containsKey(key)

    /**
     * Gets stats on all entries.
     */
    override fun stats():List<CacheStats> {
        val keys = this.lruCache.keys.toList()
        return keys.mapNotNull { stats(it) }
    }

    /**
     * Gets stats on all entries.
     */
    override fun stats(key:String):CacheStats? {
        val entry = lruCache[key]
        return entry?.let {
            val stats = accesses.get(it.key)
            val accesses = stats?.first
            val misses = stats?.second
            it.stats(accesses, misses)
        }
    }

    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    override fun refresh(key: String): Outcome<Boolean> {
        return operate(key, CacheAction.Refresh) {
            it.refresh()
        }
    }

    /**
     * invalidates a specific cache item with the key
     */
    override fun expire(key: String):Outcome<Boolean> {
        return operate(key, CacheAction.Expire) {
            it.expire()
        }
    }

    /**
     * invalidates all the entries in this cache by maxing out their expiration times
     */
    override fun expireAll():Outcome<Boolean> {
        val results = lruCache.keys.toList().map { key -> expire(key) }
        notify(CacheAction.ExpireAll)
        return when(results.all { it.success }) {
            true  -> results.first()
            false -> results.first { !it.success }
        }
    }

    /**
     * delete a single cache item with the key
     *
     * @param key
     */
    override fun delete(key: String): Outcome<Boolean> {
        return operate(key, CacheAction.Delete) {
            lruCache.remove(key)
            Outcomes.success(true)
        }
    }

    /**
     * delete all items from cache
     *
     * @param key
     */
    override fun deleteAll(): Outcome<Boolean> {
        val results = lruCache.keys.toList().map { key -> delete(key) }
        notify(CacheAction.DeleteAll)
        return when(results.all { it.success }) {
            true  -> results.first()
            false -> results.first { !it.success }
        }
    }

    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    fun getEntry(key: String): CacheValue? = lruCache.get(key)?.item?.get()

    /**
     * creates a cache item
     *
     * @param key : The name of the cache key
     * @param seconds : The expiration time in seconds
     * @param fetcher : The function to fetch the data ( will be wrapped in a Future )
     * @tparam T
     */
    override fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?) {
        insert(key, desc, seconds, fetcher)
        notify(CacheAction.Create, key)
    }

    /**
     * creates a cache item
     *
     * @param key : The name of the cache key
     * @param fetcher : The function to fetch the data ( will be wrapped in a Future )
     * @tparam T
     */
    override fun <T> set(key: String, value:T?) {
        val cacheValue = value
        val content = cacheValue?.toString() ?: ""
        val entry = lruCache[key]
        entry?.let {
            it.set(cacheValue, content)
        }
        notify(CacheAction.Update, key)
    }

    /**
     * gets a cache item or loads it if not available, via a future
     *
     * @param key
     * @tparam T
     * @return
     */
    protected fun <T> getInternal(key: String, load:Boolean): T? {

        // Stats: Update ACCESS count
        accesses[key]?.let { it.first.inc() }

        val entry = lruCache.get(key)
        val result = when(entry) {
            null -> {
                // Stats: Update MISS count
                accesses[key]?.let { it.second.inc() }
                null
            }
            else -> {
                val value = if (entry.isAlive()) {
                    val value = entry.item.get()

                    // Stats: Update HITS count
                    value.hits.inc()

                    val tracked = value.value
                    tracked.value()
                } else if(load){

                    entry.refresh()
                    val value = entry.item.get()

                    // Stats: Update HITS count
                    value.hits.inc()

                    val tracked = value.value
                    tracked.value()
                } else {
                    null
                }
                value
            }
        }
        @Suppress("UNCHECKED_CAST")
        return result?.let { r -> r as T }
    }


    private fun notify(action:CacheAction, key:String? = null){
        notify(CacheEvent.of(id, action, key ?: ""))
    }


    private fun notify(event:CacheEvent){
        Cache.notify(event, listener, logger)
    }

    private fun <T> operate(key:String, action: CacheAction, op: (CacheEntry) -> Outcome<T>): Outcome<T> {
        return when(val entry = lruCache[key]) {
            null -> Outcomes.invalid("Cache Key $key not found")
            else -> {
                val result = op(entry)
                if(result.success) {
                    notify(action, key)
                }
                result
            }
        }
    }

    protected fun insert(
        key: String,
        desc: String,
        seconds: Int,
        fetcher: suspend () -> Any?
    ) {

        val stats = Pair(Tracker<Any>(settings.updateCount), Tracker<Any>(settings.updateCount))
        accesses[key] = stats

        val entry = CacheEntry(key, desc, seconds, fetcher)
        lruCache[key] = entry
        entry.refresh()
    }
}
