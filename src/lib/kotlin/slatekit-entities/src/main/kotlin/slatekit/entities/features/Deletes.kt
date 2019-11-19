package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.core.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Deletes<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean {
        val success = repo().delete(entity)

        // Event out
        if (entity != null && this is Hooks) {
            when (success) {
                true -> this.onEntityEvent(EntityEvent.EntityDeleted(entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to delete: " + entity.toString()), DateTime.now()))
            }
        }

        return success
    }

    /**
     * updates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun deleteAsTry(entity: T): Try<Boolean> {
        return Tries.attempt { delete(entity) }
    }

    /**
     * deletes the entity by its id
     * @param id
     * @return
     */
    fun deleteById(id: TId): Boolean {
        return repo().deleteById(id)
    }

    /**
     * deletes the entities
     *
     * @param entity
     */
    fun deleteByIds(ids: List<TId>): Int {

        // Event out one by one
        return if (this is Hooks) {
            val statuses = ids.map { id -> repo().getById(id)?.let { delete(it) } ?: false }
            statuses.count { it }
        } else {
            repo().deleteByIds(ids)
        }
    }

    /**
     * deletes all the items in the table
     * @return
     */
    fun deleteAll(): Long {
        return repo().deleteAll()
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return repo().deleteByField(prop.name, Op.Eq, value)
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, op: Op, value: Any): Int {
        return repo().deleteByField(prop.name, op, value)
    }

    /**
     * updates items using the query
     */
    fun deleteByQuery(query: IQuery): Int {
        return repo().deleteByQuery(query)
    }
}
