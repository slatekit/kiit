package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntityCreates<T> : ServiceSupport<T> where T : Entity {

    /**
     * creates the entity in the datastore
     * @param entity
     * @return
     */
    fun create(entity: T): Long {
        val finalEntity = applyFieldData(1, entity)
        return entityRepo().create(finalEntity)
    }
}