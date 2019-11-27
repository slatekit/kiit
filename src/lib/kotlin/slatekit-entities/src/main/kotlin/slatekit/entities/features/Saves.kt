package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.events.EntityAction
import slatekit.entities.events.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.entities.events.EntityHooks
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Saves<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?): Try<TId> {
        val result = try {
            entity?.let { item ->
                val saveResult = when (item.isPersisted()) {
                    false -> {
                        val finalEntity = applyFieldData(EntityAction.Create, item)
                        val id = repo().create(finalEntity)
                        if (isCreated(id)) Tries.success(id) else Tries.errored("Error creating item")
                    }
                    true -> {
                        val finalEntity = applyFieldData(EntityAction.Update, item)
                        val updated = repo().update(finalEntity)
                        if (updated) Tries.success(finalEntity.identity()) else Tries.errored("Error updating item")
                    }
                }
                // Event out
                saveResult.onSuccess {
                    if (this is EntityHooks) {
                        this.onEntityEvent(EntityEvent.EntitySaved(item.identity(), item, DateTime.now()))
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
    fun saveAll(items: List<T>) {
        // Event out
        if (this is EntityHooks) {
            items.forEach { save(it) }
        } else {
            repo().saveAll(items)
        }
    }
}
