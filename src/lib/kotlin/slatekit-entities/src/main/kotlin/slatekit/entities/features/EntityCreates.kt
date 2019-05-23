package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.core.EntityAction
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.results.Outcome
import slatekit.results.Try
import java.util.*

interface EntityCreates<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * creates the entity in the datastore
     * @param entity
     * @return
     */
    fun create(entity: T): TId {
        // Massage with timestamps
        val entityWithData = applyFieldData(EntityAction.EntityCreate, entity)

        // Create! get id
        val id = repoT().create(entityWithData)

        // Event out
        if (this is EntityHooks) {
            val success = isCreated(id)
            when (success) {
                true -> this.onEntityEvent(EntityEvent.EntityCreated(id, entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to create: " + entity.toString()), DateTime.now()))
            }
        }

        return id
    }


    /**
     * creates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun createAsTry(entity: T): Try<TId> {
        return Try.attempt { create(entity) }
    }
}