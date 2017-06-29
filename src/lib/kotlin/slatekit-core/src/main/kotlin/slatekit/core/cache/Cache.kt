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

package slatekit.core.cache

import TODO
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
class Cache(opts: CacheSettings) : ICache {

    override val settings = opts
    private val _lookup = ConcurrentHashMap<String, CacheEntry>()


    /**
     * size of the cache
     *
     * @return
     */
    override fun size(): Int = _lookup.size


    /**
     * whether this cache contains the entry with the supplied key
     *
     * @param key
     * @return
     */
    override fun contains(key: String): Boolean = _lookup.contains(key)


    /**
     * invalidates all the entries in this cache by maxing out their expiration times
     */
    override fun invalidateAll(): Unit = _lookup.keys.toList().forEach { key -> invalidate(key) }


    /**
     * invalidates a specific cache item with the key
     */
    override fun invalidate(key: String): Unit {
        _lookup.get(key)?.let { c -> c.invalidate() }
    }


    /**
     * remove a single cache item with the key
     *
     * @param key
     */
    override fun remove(key: String): Boolean = _lookup.remove(key)?.let { k -> true } ?: false


    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    override fun getCacheItem(key: String): CacheItem? =
            _lookup.get(key)?.item?.get()


    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @tparam T
     * @return
     */
    override fun <T> get(key: String): T? {
        val result = _lookup.get(key)?.let { c ->
            if (c.isAlive()) {
                c.item.get().value
            }
            else {
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
        TODO.IMPLEMENT {

        }
        return null
    }


    /**
     * manual / explicit refresh of a cache item with a future result
     * in order to get the item
     *
     * @param key
     */
    override fun <T> getFresh(key: String): T? {
        TODO.IMPLEMENT {

        }
        return null
    }


    /**
     * gets a cache item associated with the key
     *
     * @param key
     * @return
     */
    override fun refresh(key: String): Unit {
        _lookup.get(key)?.refresh()
    }


    /**
     * creates a cache item
     *
     * @param key       : The name of the cache key
     * @param seconds   : The expiration time in seconds
     * @param fetcher   : The function to fetch the data ( will be wrapped in a Future )
     * @tparam T
     */
    override fun <T> put(key: String, desc: String, seconds: Int, fetcher: () -> T?): Unit {

        TODO.IMPLEMENT {

        }
    }


    private fun insert(key: String,
                       text: String?,
                       seconds: Int,
                       fetcher: () -> Any): Unit {
        val entry = CacheEntry(key, text, seconds, fetcher)
        _lookup[key] = entry
        entry.refresh()
    }
}
