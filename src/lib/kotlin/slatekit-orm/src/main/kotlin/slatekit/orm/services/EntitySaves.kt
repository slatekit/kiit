package slatekit.orm.services

import slatekit.orm.core.Entity
import slatekit.orm.core.ServiceSupport

interface EntitySaves<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?) {
        entity?.let { item ->
            val finalEntity = applyFieldData(3, item)
            entityRepo().save(finalEntity)
        }
    }

    /**
     * saves all the entities
     *
     * @param items
     */
    fun saveAll(items: List<T>) {
        entityRepo().saveAll(items)
    }
}