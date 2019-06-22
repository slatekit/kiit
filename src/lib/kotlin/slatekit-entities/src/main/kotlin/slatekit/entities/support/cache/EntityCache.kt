package slatekit.entities.support.cache

import slatekit.common.utils.Batch
import slatekit.entities.Entity
import slatekit.entities.EntityService
import slatekit.entities.support.Cacheable

/**
 * Default / Simple implementation of a cache that loads
 * entries from the supplied EntityService.
 *
 * This enriches the Cacheable interface with additional
 * operations to get batches of data using the Batch<T> interface.
 */
open class EntityCache<TId, TKey, T>(
        val settings: EntityCacheSettings<TId, TKey, T>,
        val load:Boolean) : Batch<T>, Cacheable<TId, TKey, T>
        where TId : Comparable<TId>, T: Entity<TId> {

    /**
     * Convenience constructor
     */
    constructor(service:EntityService<TId, T>,
                keyLookup:(T) -> TKey,
                fetcher: (EntityService<TId, T>) -> List<T>,
                load:Boolean) : this(EntityCacheSettings(service, keyLookup, fetcher), load)


    protected var cacheContents = if(load) load(settings) else CacheContents()


    /**
     * Gets the current cache contents
     */
    override fun contents(): CacheContents<T> {
        return cacheContents
    }


    /**
     * Refreshes the internal cache
     */
    override fun refresh() {
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
        fun <TId, TKey, T> load(settings: EntityCacheSettings<TId, TKey, T>, fetcher:((EntityService<TId, T>) -> List<T>)? = null): CacheContents<T>
                where TId : Comparable<TId>, T: Entity<TId> {
            val itemList = fetcher?.invoke(settings.service) ?: settings.service.getAll()
            val itemIdMap = itemList.map { Pair(it.identity().toString(), it) }.toMap()
            val itemKeyMap = itemList.map { Pair( settings.keyLookup(it).toString(), it)}.toMap()
            val itemListCopy = itemList.toList()
            val contents = CacheContents<T>(itemListCopy, itemList, itemIdMap, itemKeyMap)
            return contents
        }
    }
}