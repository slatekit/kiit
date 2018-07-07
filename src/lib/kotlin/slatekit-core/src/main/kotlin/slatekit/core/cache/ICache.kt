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


interface ICache {

    /**
     * settings for the cache
     */
    val settings: CacheSettings


    /**
     * number of items in the cache
     * @return
     */
    fun size(): Int


    /**
     * gets the keys in the cache
     */
    fun keys(): List<String>


    /**
     * whether the cache contains the key
     * @param key
     * @return
     */
    fun contains(key: String): Boolean


    /**
     * gets the cache entry itself.
     * NOTE: This is exposed
     * @param key
     * @return
     */
    fun getEntry(key: String): CacheItem?


    /**
     * gets an item from the cache if it exists and is alive
     * @param key
     * @tparam T
     * @return
     */
    fun <T> get(key: String): T?


    /**
     * gets an item from the cache as a future
     * @param key
     * @tparam T
     * @return
     */
    fun <T> getOrLoad(key: String): T?


    /**
     * manual / explicit refresh of a cache item with a future result
     * in order to get the item
     * @param key
     */
    fun <T> getFresh(key: String): T?


    /**
     * puts an item in the cache and loads it immediately
     * @param key
     * @param desc
     * @param seconds
     * @param fetcher
     * @tparam T
     */
    fun <T> put(key: String, desc: String, seconds: Int, fetcher: () -> T?): Unit


    /**
     * removes a item from the cache
     * @param key
     * @return
     */
    fun remove(key: String): Boolean


    /**
     * removes all items from the cache
     */
    fun clear(): Boolean


    /**
     * manual / explicit refresh of a cache item
     * @param key
     */
    fun refresh(key: String)


    /**
     * invalidates a single cache item by its key
     * @param key
     */
    fun invalidate(key: String)


    /**
     * invalidates all the cache items
     */
    fun invalidateAll()
}
