package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.entities.slatekit.entities.EntityOptions
import slatekit.results.Try
import slatekit.results.builders.Tries


interface EntityWrites<TId, T> : ServiceSupport<TId, T>,
        EntityCreates<TId, T>,
        EntityUpdates<TId, T>,
        EntityDeletes<TId, T>,
        EntitySaves<TId, T>

        where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?, options: EntityOptions): Try<Pair<TId, T>> {
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
        } catch(ex:Exception) {
            Tries.errored<Pair<TId, T>>(ex)
        }
        return result
    }
}