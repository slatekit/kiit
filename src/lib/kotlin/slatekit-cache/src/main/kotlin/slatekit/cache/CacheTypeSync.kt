package slatekit.cache

interface CacheTypeSync : Cache {
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
}
