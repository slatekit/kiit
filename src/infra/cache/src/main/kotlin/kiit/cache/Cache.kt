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
import slatekit.results.Outcome

interface Cache {
    /**
     * Identity of the this cache, used to identify it between multiple caches
     * @see[slatekit.common.Identity]
     */
    val id: Identity

    /**
     * settings for the cache
     */
    val settings: CacheSettings

    /**
     * Listener for cache events which describe past activity on the cache.
     * Serves as a simple event stream for this cache
     */
    val listener: ((CacheEvent) -> Unit)?

    /**
     * Logger for warnings/errors
     */
    val logger: Logger?

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
     * Gets stats on all entries
     */
    fun stats(): List<CacheStats>

    /**
     * Gets stats on a specific entry
     */
    fun stats(key: String): CacheStats?

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
    fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?)

    /**
     * Sets an explict value for the entry
     */
    fun <T> set(key: String, value: T?)

    /**
     * manual / explicit refresh of a cache item
     * @param key
     */
    fun refresh(key: String): Outcome<Boolean>

    /**
     * invalidates a single cache item by its key
     * @param key
     */
    fun expire(key: String): Outcome<Boolean>

    /**
     * invalidates all the cache items
     */
    fun expireAll(): Outcome<Boolean>

    /**
     * removes a item from the cache
     * @param key
     * @return
     */
    fun delete(key: String): Outcome<Boolean>

    /**
     * removes all items from the cache
     */
    fun deleteAll(): Outcome<Boolean>


    companion object {

        fun notify(event: CacheEvent, listener: ((CacheEvent) -> Unit)?, logger: Logger?) {
            // E.g. origin=service1-cache, uuid=abc123, action=delete, key=some-data
            val pairs = event.structured()
            val info = pairs.joinToString { "${it.first}=${it.second}" }

            try {
                logger?.info("Cache event: $info")
                listener?.invoke(event)
            } catch (ex: Exception) {
                logger?.warn("Cache error while notifying: $info")
            }
        }
    }
}



