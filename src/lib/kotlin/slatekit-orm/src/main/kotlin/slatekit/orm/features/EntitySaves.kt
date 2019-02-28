package slatekit.orm.features

import slatekit.common.DateTime
import slatekit.orm.core.Entity
import slatekit.orm.core.EntityEvent
import slatekit.orm.core.ServiceSupport
import slatekit.orm.slatekit.orm.features.EntityHooks

interface EntitySaves<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

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