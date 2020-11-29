package slatekit.cache

import kotlinx.coroutines.Deferred
import slatekit.common.log.Logger
import slatekit.results.Outcome

interface AsyncCache {
    /**
     * Name of the this cache, used to identify it between multiple caches
     */
    val name:String

    /**
     * settings for the cache
     */
    val settings: CacheSettings

    /**
     * Listener for cache events which describe past activity on the cache.
     * Serves as a simple event stream for this cache
     */
    val listener:((CacheEvent) -> Unit)?

    /**
     * Logger for warnings/errors
     */
    val logger: Logger?

    /**
     * number of items in the cache
     * @return
     */
    suspend fun size(): Int

    /**
     * gets the keys in the cache
     */
    suspend fun keys(): List<String>

    /**
     * whether the cache contains the key
     * @param key
     * @return
     */
    suspend fun contains(key: String): Boolean

    /**
     * Gets stats on all entries
     */
    suspend fun stats():List<CacheStats>

    /**
     * puts an item in the cache and loads it immediately
     * @param key
     * @param desc
     * @param seconds
     * @param fetcher
     * @tparam T
     */
    suspend fun <T> put(key: String, desc: String, seconds: Int, fetcher: suspend () -> T?)

    /**
     * Sets an explict value for the entry
     */
    suspend fun <T> set(key: String, value:T?)

    /**
     * gets an item from the cache if it exists and is alive
     * @param key
     * @tparam T
     * @return
     */
    suspend fun <T> getAsync(key: String): Deferred<T?>

    /**
     * gets an item from the cache as a future
     * @param key
     * @tparam T
     * @return
     */
    suspend fun <T> getOrLoadAsync(key: String): Deferred<T?>

    /**
     * manual / explicit refresh of a cache item with a future result
     * in order to get the item
     * @param key
     */
    suspend fun <T> getFreshAsync(key: String): Deferred<T?>

    /**
     * gets an item from the cache if it exists and is alive
     * @param key
     * @tparam T
     * @return
     */
    suspend fun <T> get(key: String): T?

    /**
     * gets an item from the cache as a future
     * @param key
     * @tparam T
     * @return
     */
    suspend fun <T> getOrLoad(key: String): T?

    /**
     * manual / explicit refresh of a cache item with a future result
     * in order to get the item
     * @param key
     */
    suspend fun <T> getFresh(key: String): T?

    /**
     * manual / explicit refresh of a cache item
     * @param key
     */
    suspend fun refresh(key: String): Outcome<Boolean>

    /**
     * invalidates a single cache item by its key
     * @param key
     */
    suspend fun expire(key: String): Outcome<Boolean>

    /**
     * invalidates all the cache items
     */
    suspend fun expireAll(): Outcome<Boolean>

    /**
     * removes a item from the cache
     * @param key
     * @return
     */
    suspend fun delete(key: String): Outcome<Boolean>

    /**
     * removes all items from the cache
     */
    suspend fun deleteAll(): Outcome<Boolean>
}
