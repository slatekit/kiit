package kiit.entities.support.cache

import kiit.common.utils.Batch
import kiit.entities.Entity
import kiit.entities.EntityService
import kiit.entities.support.Cacheable

/**
 * Default / Simple implementation of a cache that loads
 * entries from the supplied EntityService.
 *
 * This enriches the Cacheable interface with additional
 * operations to get batches of data using the Batch<T> interface.
 */
open class EntityCache<TId, TKey, T> private constructor(
    val settings: EntityCacheSettings<TId, TKey, T>,
    contents:CacheContents<T>
) : Batch<T>, Cacheable<TId, TKey, T>
        where TId : Comparable<TId>, T : Entity<TId> {


    protected var cacheContents = contents

    /**
     * Gets the current cache contents
     */
    override fun contents(): CacheContents<T> {
        return cacheContents
    }

    /**
     * Refreshes the internal cache
     */
    override suspend fun refresh() {
        cacheContents = load(settings)
    }

    /**
     * Get all the items in the cache
     */
    override fun items(): List<T> = contents().itemListCopy

    companion object {

        /**
         * Loads the entire cache contents using settings
         */
        suspend fun <TId, TKey, T> load(settings: EntityCacheSettings<TId, TKey, T>, fetcher: ((EntityService<TId, T>) -> List<T>)? = null): CacheContents<T>
                where TId : Comparable<TId>, T : Entity<TId> {
            val itemList = fetcher?.invoke(settings.service) ?: settings.service.getAll()
            val itemIdMap = itemList.map { Pair(it.identity().toString(), it) }.toMap()
            val itemKeyMap = itemList.map { Pair(settings.keyLookup(it).toString(), it) }.toMap()
            val itemListCopy = itemList.toList()
            val contents = CacheContents<T>(itemListCopy, itemList, itemIdMap, itemKeyMap)
            return contents
        }

        /**
         * Loads the entire cache contents using settings
         */
        suspend fun <TId, TKey, T> of(settings: EntityCacheSettings<TId, TKey, T>): EntityCache<TId, TKey, T>
            where TId : Comparable<TId>, T : Entity<TId> {
            val contents = load(settings)
            return EntityCache<TId, TKey, T>(settings, contents)
        }
    }
}
