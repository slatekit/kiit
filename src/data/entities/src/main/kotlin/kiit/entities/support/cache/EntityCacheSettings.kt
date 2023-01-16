package kiit.entities.support.cache

import kiit.entities.Entity
import kiit.entities.EntityService

/**
 * Settings to handle loading of data into the cache to store by
 * 1. TId  ( primary key )
 * 2. TKey ( secondary custom field/key )
 *
 * @param service : The EntityService to load the data from
 * @param keyLookup: Function to provide the key for indexing
 * @param fetcher : Function to control how to load data into cache using service
 */
data class EntityCacheSettings<TId, TKey, T>(
    val service: EntityService<TId, T>,
    val keyLookup: (T) -> TKey,
    val fetcher: (EntityService<TId, T>) -> List<T>
)
        where TId : Comparable<TId>, T : Entity<TId>
