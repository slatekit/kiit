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

import slatekit.common.log.Logger
import slatekit.common.utils.Random
import slatekit.tracking.Fetches

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
open class SimpleCache(override val name:String = Random.uuid(),
                       override val settings: CacheSettings,
                       override val listener:((CacheEvent) -> Unit)? = null,
                       override val logger: Logger? = null ) : Cache, SyncCache {

    /**
     * The LinkedHashMap already LRU(Least Recently Used) behaviour out of the box.
     */
    protected val lookup = LRUMap<String, CacheEntry>(settings.size)

    /**
     * Stats to record cache accesses and cache misses.
     * CacheEntry only records hits as it only exist if there is an entry for the key
     */
    protected val accesses  = LRUMap<String, Pair<Fetches, Fetches>>(settings.size)


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
    override fun size(): Int = lookup.size

    /**
     * size of the cache
     *
     * @return
     */
    override fun keys(): List<String> = lookup.keys.map { it }

    /**
     * whether this cache contains the entry with the supplied key
     *
     * @param key
     * @return
     */
    override fun contains(key: String): Boolean = lookup.containsKey(key)

    /**
     * Gets stats on all entries.
     */
    override fun stats():List<CacheStats> {

        val allStats = this.lookup.values.map {
            val stats = accesses.get(it.key)
            val accesses = stats?.first
            val misses = stats?.second
            it.stats(accesses, misses)
        }
        return allStats
    }


    /**
     * invalidates all the entries in this cache by maxing out their expiration times
     */
    override fun invalidateAll() {
        lookup.keys.toList().forEach { key -> invalidate(key) }
        notify(CacheAction.Clear)
    }

    /**
     * invalidates a specific cache item with the key
     */
    override fun invalidate(key: String) {
        lookup.get(key)?.let { c -> c.expire() }
        notify(CacheAction.Clear, key)
    }

    /**
     * remove all items from cache
     *
     * @param key
     */
    override fun clear(): Boolean {
        val result = lookup.keys.toList().map { key -> remove(key) }.reduceRight({ r, a -> a })
        notify(CacheAction.Clear)
        return result
    }

    /**
     * remove a single cache item with the key
     *
     * @param key
     */
    override fun remove(key: String): Boolean {
        val result = lookup.remove(key)?.let { k -> true } ?: false
        notify(CacheAction.Delete, key)
        return result
    }

    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    fun getEntry(key: String): CacheValue? = lookup.get(key)?.item?.get()


    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    override fun refresh(key: String) {
        lookup.get(key)?.refresh()
        notify(CacheAction.Refresh, key)
    }

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
        val entry = lookup[key]
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

        val entry = lookup.get(key)
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
                    tracked.get().current
                } else if(load){

                    entry.refresh()
                    val value = entry.item.get()

                    // Stats: Update HITS count
                    value.hits.inc()

                    val tracked = value.value
                    tracked.get().current
                } else {
                    null
                }
                value
            }
        }
        @Suppress("UNCHECKED_CAST")
        return result?.let { r -> r as T }
    }


    protected fun notify(action:CacheAction, key:String? = null){
        notify(CacheEvent.of(name, action, key ?: ""))
    }


    protected fun notify(event:CacheEvent){
        Cache.notify(event, listener, logger)
    }


    protected fun insert(
        key: String,
        desc: String,
        seconds: Int,
        fetcher: suspend () -> Any?
    ) {

        val stats = Pair(Fetches(settings.updateCount), Fetches(settings.updateCount))
        accesses[key] = stats

        val entry = CacheEntry(key, desc, seconds, fetcher)
        lookup[key] = entry
        entry.refresh()
    }
}
