package slatekit.cache

/**
 * Core management operations on the cache.
 * NOTE: The default implementation uses a Channel based
 * approach to effectively make cache operations synchronous on write
 */
interface Management {
    /**
     * Requests clearing of all cache entries
     */
    suspend fun clear() = request(CacheCommand.ClearAll)

    /**
     * Requests deletion of a cache entry
     */
    suspend fun clear(key:String) = request(CacheCommand.Clear(key))

    /**
     * Requests checking of a cache entry
     */
    suspend fun check(key:String) = request(CacheCommand.Check(key))

    /**
     * Requests deletion of a cache entry
     */
    suspend fun delete(key:String) = request(CacheCommand.Del(key))

    /**
     * Requests refreshing of a cache entry
     */
    suspend fun refresh(key:String) = request(CacheCommand.Refresh(key))

    /**
     * Requests insertion of a cache entry
     */
    suspend fun put(key:String, expiryInSeconds:Int, fetcher:suspend() -> Any?) = request(CacheCommand.Put(key, "", expiryInSeconds, fetcher))

    /**
     * Requests explicitly setting a cache entry value
     */
    suspend fun set(key:String, expiryInSeconds:Int, value:Any?) = request(CacheCommand.Set(key, expiryInSeconds, value))

    /**
     * Requests fetching of a cache entry
     */
    suspend fun get(key:String, onReady:suspend(Any?) -> Unit ) = request(CacheCommand.Get(key, onReady))

    /**
     * Requests a cache operation
     */
    suspend fun request(command: CacheCommand)
}
