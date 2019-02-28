package slatekit.orm.features

import slatekit.common.DateTime
import slatekit.orm.core.Entity
import slatekit.orm.core.EntityEvent
import slatekit.orm.core.ServiceSupport
import slatekit.orm.slatekit.orm.features.EntityHooks
import java.util.*

interface EntityCreates<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * creates the entity in the datastore
     * @param entity
     * @return
     */
    fun create(entity: T): TId {
        // Massage with timestamps
        val entityWithData = applyFieldData(1, entity)

        // Create! get id
        val id = repoT().create(entityWithData)

        // Event out
        if ( this is EntityHooks ) {
            val success = isCreated(id)
            when(success) {
                true -> this.onEntityEvent(EntityEvent.EntityCreated(id, entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to create: " + entity.toString()), DateTime.now()))
            }
        }

        return id
    }


    fun isCreated(id:TId):Boolean {
        return when(id) {
            is Int    -> id > 0
            is Long   -> id > 0L
            is String -> !id.isEmpty()
            is UUID   -> !id.toString().trim().isEmpty()
            else      -> false
        }
    }
}