package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.query.IQuery
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport
import kotlin.reflect.KProperty

interface EntityDeletes<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean {
        val success = repoT().delete(entity)

        // Event out
        if ( entity != null && this is EntityHooks ) {
            when(success) {
                true -> this.onEntityEvent(EntityEvent.EntityDeleted(entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to delete: " + entity.toString()), DateTime.now()))
            }
        }

        return success
    }

    /**
     * deletes the entities
     *
     * @param entity
     */
    fun delete(ids: List<TId>): Int {

        // Event out one by one
        return if ( this is EntityHooks ) {
            val statuses = ids.map { id -> repoT().get(id)?.let { delete(it) } ?: false }
            statuses.count { it }
        } else {
            repoT().delete(ids)
        }
    }

    /**
     * deletes the entity by its id
     * @param id
     * @return
     */
    fun deleteById(id: TId): Boolean {
        return repoT().delete(id)
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return repoT().deleteByField(prop.name, value)
    }

    /**
     * updates items using the query
     */
    fun deleteByQuery(query: IQuery): Int {
        return repoT().deleteByQuery(query)
    }
}