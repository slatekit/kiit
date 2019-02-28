package slatekit.orm.services

import slatekit.orm.core.Entity
import slatekit.orm.core.ServiceSupport

interface EntityCreates<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * creates the entity in the datastore
     * @param entity
     * @return
     */
    fun create(entity: T): TId {
        val finalEntity = applyFieldData(1, entity)
        return entityRepo().create(finalEntity)
    }
}