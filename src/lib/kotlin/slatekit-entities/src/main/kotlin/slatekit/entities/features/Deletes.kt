package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.entities.Entity
import slatekit.entities.core.EntityOps
import slatekit.query.IQuery
import slatekit.common.data.Op
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Deletes<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean {
        return repo().delete(entity)
    }

    /**
     * updates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun deleteAsTry(entity: T): Try<Boolean> {
        return Tries.of { delete(entity) }
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
     */
    fun deleteByIds(ids: List<TId>): Int {
        return repo().deleteByIds(ids)
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
