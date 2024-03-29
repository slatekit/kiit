package kiit.entities.features

import kiit.common.data.DataAction
import kiit.entities.Entity
import kiit.entities.core.EntityOps
import kiit.results.Try
import kiit.results.builders.Tries

interface Saves<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    suspend fun save(entity: T?): Try<TId> {
        val result = try {
            entity?.let { item ->
                val saveResult = when (item.isPersisted()) {
                    false -> {
                        val finalEntity = applyFieldData(DataAction.Create, item)
                        val id = repo().create(finalEntity)
                        if (isCreated(id)) Tries.success(id) else Tries.errored("Error creating item")
                    }
                    true -> {
                        val finalEntity = applyFieldData(DataAction.Update, item)
                        val updated = repo().update(finalEntity)
                        if (updated) Tries.success(finalEntity.identity()) else Tries.errored("Error updating item")
                    }
                }
                saveResult
            } ?: Tries.errored("Entity not provided")
        } catch (ex: Exception) {
            Tries.errored<TId>(ex)
        }
        return result
    }

    /**
     * saves all the entities
     *
     * @param items
     */
    suspend fun saveAll(items: List<T>):List<Pair<TId?, Boolean>> {
        return repo().saveAll(items)
    }
}
