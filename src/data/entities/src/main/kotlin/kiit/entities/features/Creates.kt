package kiit.entities.features

import slatekit.common.data.DataAction
import kiit.entities.Entity
import kiit.entities.EntityUpdatable
import kiit.entities.core.EntityOps
import kiit.entities.EntityOptions
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Creates<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * creates an entity in the data store without applying field data and sending events via Hooks
     * @param entity
     * @return
     */
    suspend fun insert(entity: T): TId {
        return repo().create(entity)
    }

    /**
     * creates the entity in the data store with additional processing based on the options supplied
     * @param entity : The entity to save
     * @param options: Settings to determine whether to apply metadata, and notify via Hooks
     */
    suspend fun create(entity: T, options: EntityOptions): Pair<TId, T> {
        // Massage
        val entityWithMeta = when (options.applyMetadata) {
            true -> applyFieldData(DataAction.Create, entity)
            false -> entity
        }

        // Create! get id
        val id = insert(entityWithMeta)

        // Update id
        val entityFinal = when (options.applyId && entityWithMeta is EntityUpdatable<*, *>) {
            true -> entityWithMeta.withIdAny(id) as T
            false -> entityWithMeta
        }
        return Pair(id, entityFinal)
    }

    /**
     * creates the entity in the data store and sends an event if there is support for Hooks
     * @param entity
     * @return
     */
    suspend fun create(entity: T): TId {
        // Massage with timestamps
        val entityWithData = applyFieldData(DataAction.Create, entity)

        // Create! get id
        val id = repo().create(entityWithData)
        return id
    }

    /**
     * creates the entity in the data store and updates its id with the one generated
     * @param entity
     * @return
     */
    suspend fun createWithId(entity: T): T {
        val id = create(entity)

        // Update id
        return when (entity is EntityUpdatable<*, *>) {
            true -> entity.withIdAny(id) as T
            false -> entity
        }
    }

    /**
     * creates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    suspend fun createAsTry(entity: T): Try<TId> {
        return Tries.of {
            create(entity)
        }
    }
}
