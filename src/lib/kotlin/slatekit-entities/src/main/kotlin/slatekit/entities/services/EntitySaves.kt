package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntitySaves<T> : ServiceSupport<T> where T : Entity {

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