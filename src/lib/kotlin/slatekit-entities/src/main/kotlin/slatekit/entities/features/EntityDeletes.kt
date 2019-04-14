package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.query.IQuery
import slatekit.entities.Entity
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.query.Op
import kotlin.reflect.KProperty

interface EntityDeletes<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

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
     * deletes the entity by its id
     * @param id
     * @return
     */
    fun deleteById(id: TId): Boolean {
        return repoT().delete(id)
    }

    /**
     * deletes the entities
     *
     * @param entity
     */
    fun deleteByIds(ids: List<TId>): Int {

        // Event out one by one
        return if ( this is EntityHooks ) {
            val statuses = ids.map { id -> repoT().get(id)?.let { delete(it) } ?: false }
            statuses.count { it }
        } else {
            repoT().delete(ids)
        }
    }

    /**
     * deletes all the items in the table
     * @return
     */
    fun deleteAll(): Long {
        return repoT().deleteAll()
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return repoT().deleteByField(prop.name, Op.Eq, value)
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, op:Op, value: Any): Int {
        return repoT().deleteByField(prop.name, op, value)
    }

    /**
     * updates items using the query
     */
    fun deleteByQuery(query: IQuery): Int {
        return repoT().deleteByQuery(query)
    }
}