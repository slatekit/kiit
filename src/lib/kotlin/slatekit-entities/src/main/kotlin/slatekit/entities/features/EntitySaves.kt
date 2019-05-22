package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.core.EntityAction
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.results.Try
import slatekit.results.builders.Tries

interface EntitySaves<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

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
                        val finalEntity = applyFieldData(EntityAction.EntityCreate, item)
                        val id = repoT().create(finalEntity)
                        if (isCreated(id)) Tries.success(id) else Tries.errored("Error creating item")
                    }
                    true -> {
                        val finalEntity = applyFieldData(EntityAction.EntityUpdate, item)
                        val updated = repoT().update(finalEntity)
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
        } catch(ex:Exception) {
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
        if ( this is EntityHooks) {
            items.forEach { save(it) }
        } else {
            repoT().saveAll(items)
        }
    }
}