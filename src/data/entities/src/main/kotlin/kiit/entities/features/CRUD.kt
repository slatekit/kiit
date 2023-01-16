package kiit.entities.features

import kiit.entities.Entity
import kiit.entities.core.EntityOps
import kiit.entities.EntityOptions
import slatekit.results.Try
import slatekit.results.builders.Tries

interface CRUD<TId, T> : EntityOps<TId, T>,
        Creates<TId, T>,
        Reads<TId, T>,
        Updates<TId, T>,
        Deletes<TId, T>,
        Finds<TId, T>,
        Saves<TId, T>

        where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    suspend fun save(entity: T?, options: EntityOptions): Try<Pair<TId, T>> {
        val result = try {
            entity?.let { item ->
                val saveResult = when (item.isPersisted()) {
                    false -> {
                        val info = create(item, options)
                        val id = info.first
                        if (isCreated(id)) Tries.success(info) else Tries.errored("Error creating item")
                    }
                    true -> {
                        val info = update(item, options)
                        val updated = info.first
                        val id = info.second.identity()
                        if (updated) Tries.success(Pair(id, info.second)) else Tries.errored("Error updating item")
                    }
                }
                saveResult
            } ?: Tries.errored("Entity not provided")
        } catch (ex: Exception) {
            Tries.errored<Pair<TId, T>>(ex)
        }
        return result
    }
}
