package slatekit.entities.support

import slatekit.entities.Entity
import slatekit.entities.support.cache.CacheContents

/**
 * Simple interface to support most of the cache operations.
 * This is here in case clients want to create their own cache implementation
 * with some level of built in functionality while also NOT wanting to use
 * the supplied EntityCache component
 */
interface Cacheable<TId, TKey, T>
        where TId : Comparable<TId>, T : Entity<TId> {

    /**
     * Gets the cache contents
     */
    fun contents(): CacheContents<T>

    /**
     * Refreshes the internal cache
     */
    fun refresh()

    /**
     * Size of the entries in the cache
     */
    fun size(): Int = contents().itemList.size

    /**
     * Gets a cache entry by its index
     */
    operator fun get(ndx: Int): T? {
        return if (ndx < 0 || ndx >= contents().itemList.size) null else contents().itemList[ndx]
    }

    /**
     * Gets an single item by its id ( primary key )
     */
    fun getById(id: TId): T? {
        return if (contents().itemIdMap.containsKey(id.toString())) contents().itemIdMap[id.toString()] else null
    }

    /**
     * Gets a cache entry by its index
     */
    fun getByKey(key: TKey): T? {
        val keyText = key.toString()
        val result = if (containsKey(keyText)) contents().itemKeyMap.get(keyText) else null
        return result
    }

    /**
     * Checks whether or an entry exists by its id
     */
    operator fun contains(id: TId): Boolean {
        return contents().itemIdMap.containsKey(id.toString())
    }

    /**
     * Checks whether an entry exists by its key
     */
    fun containsKey(key: String): Boolean {
        return contents().itemKeyMap.containsKey(key)
    }
}
