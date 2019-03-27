package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport

interface EntitySaves<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?) {
        entity?.let { item ->
            val finalEntity = applyFieldData(3, item)
            repoT().save(finalEntity)

            // Event out
            if ( this is EntityHooks) {
                this.onEntityEvent(EntityEvent.EntitySaved(item.identity(), item, DateTime.now()))
            }
        }
    }

    /**
     * saves all the entities
     *
     * @param items
     */
    fun saveAll(items: List<T>) {
        // Event out
        if ( this is EntityHooks) {
            items.forEach { save(it) }
        } else {
            repoT().saveAll(items)
        }
    }
}