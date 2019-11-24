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

import java.util.concurrent.ConcurrentHashMap

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
class SimpleCache(opts: CacheSettings) : Cache {

    override val settings = opts
    private val lookup = ConcurrentHashMap<String, CacheEntry>()

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
    override fun keys(): List<String> = lookup.keys().toList()

    /**
     * whether this cache contains the entry with the supplied key
     *
     * @param key
     * @return
     */
    override fun contains(key: String): Boolean = lookup.contains(key)

    /**
     * invalidates all the entries in this cache by maxing out their expiration times
     */
    override fun invalidateAll(): Unit = lookup.keys.toList().forEach { key -> invalidate(key) }

    /**
     * invalidates a specific cache item with the key
     */
    override fun invalidate(key: String) {
        lookup.get(key)?.let { c -> c.invalidate() }
    }

    /**
     * remove all items from cache
     *
     * @param key
     */
    override fun clear(): Boolean = lookup.keys.toList().map { key -> remove(key) }.reduceRight({ r, a -> a })

    /**
     * remove a single cache item with the key
     *
     * @param key
     */
    override fun remove(key: String): Boolean = lookup.remove(key)?.let { k -> true } ?: false

    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    override fun getEntry(key: String): CacheItem? = lookup.get(key)?.item?.get()

    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @tparam T
     * @return
     */
    override suspend fun <T> get(key: String): T? {
        val result = lookup.get(key)?.let { c ->
            if (c.isAlive()) {
                c.item.get().value
            } else {
                // Expired so kick off a refresh
                c.refresh()
            }
        }
        return result?.let { r -> r as T }
    }

    /**
     * gets a cache item or loads it if not available, via a future
     *
     * @param key
     * @tparam T
     * @return
     */
    override fun <T> getOrLoad(key: String): T? {
        val item = getEntry(key)
        item?.let { i ->
        }
        return null
    }

    /**
     * manual / explicit refresh of a cache item with a future result
     * in order to get the item
     *
     * @param key
     */
    override suspend fun <T> getFresh(key: String): T? {
        val item = lookup.get(key)
        item?.let { it ->
            it.refresh()
        }
        return null
    }

    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    override suspend fun refresh(key: String) {
        lookup.get(key)?.refresh()
    }

    /**
     * creates a cache item
     *
     * @param key : The name of the cache key
     * @param seconds : The expiration time in seconds
     * @param fetcher : The function to fetch the data ( will be wrapped in a Future )
     * @tparam T
     */
    override suspend fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?) {
        val cacheValue = fetcher()
        val content = cacheValue?.toString() ?: ""
        insert(key, desc, content, seconds, { fetcher() })
    }

    /**
     * creates a cache item
     *
     * @param key : The name of the cache key
     * @param seconds : The expiration time in seconds
     * @param fetcher : The function to fetch the data ( will be wrapped in a Future )
     * @tparam T
     */
    override fun <T> set(key: String, seconds: Int, value:T?) {
        val cacheValue = value
        val content = cacheValue?.toString() ?: ""
        val entry = lookup[key]
        entry?.let {
            it.set(cacheValue, content)
        }
    }


    private suspend fun insert(
        key: String,
        desc: String,
        text: String?,
        seconds: Int,
        fetcher: suspend () -> Any?
    ) {
        val entry = CacheEntry(key, desc, text, seconds, fetcher)
        lookup[key] = entry
        entry.refresh()
    }
}
