package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.EntityUpdatable
import slatekit.entities.core.EntityAction
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.entities.slatekit.entities.EntityOptions
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Creates<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * creates an entity in the data store without applying field data and sending events via Hooks
     * @param entity
     * @return
     */
    fun insert(entity: T): TId {
        return repo().create(entity)
    }

    /**
     * creates the entity in the data store with additional processing based on the options supplied
     * @param entity : The entity to save
     * @param options: Settings to determine whether to apply metadata, and notify via Hooks
     */
    fun create(entity: T, options: EntityOptions): Pair<TId, T> {
        // Massage
        val entityWithMeta = when (options.applyMetadata) {
            true -> applyFieldData(EntityAction.EntityCreate, entity)
            false -> entity
        }

        // Create! get id
        val id = insert(entityWithMeta)

        // Update id
        val entityFinal = when (entityWithMeta is EntityUpdatable<*, *>) {
            true -> entityWithMeta.withIdAny(id) as T
            false -> entityWithMeta
        }

        // Event out
        if (options.applyHooks && this is Hooks) {
            val success = isCreated(id)
            when (success) {
                true -> this.onEntityEvent(EntityEvent.EntityCreated(id, entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to create: " + entity.toString()), DateTime.now()))
            }
        }
        return Pair(id, entityFinal)
    }

    /**
     * creates the entity in the data store and sends an event if there is support for Hooks
     * @param entity
     * @return
     */
    fun create(entity: T): TId {
        // Massage with timestamps
        val entityWithData = applyFieldData(EntityAction.EntityCreate, entity)

        // Create! get id
        val id = repo().create(entityWithData)

        // Event out
        if (this is Hooks) {
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
        return Tries.attempt {
            create(entity)
        }
    }
}
