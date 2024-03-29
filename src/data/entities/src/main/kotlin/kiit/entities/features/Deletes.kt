package kiit.entities.features

import kotlin.reflect.KProperty
import kiit.entities.Entity
import kiit.entities.core.EntityOps
import kiit.query.Op
import kiit.query.Delete
import kiit.results.Try
import kiit.results.builders.Tries

interface Deletes<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * deletes the entity
     *
     * @param entity
     */
    suspend fun delete(entity: T?): Boolean {
        return repo().delete(entity)
    }

    /**
     * updates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    suspend fun deleteAsTry(entity: T): Try<Boolean> {
        return Tries.of { delete(entity) }
    }

    /**
     * deletes the entity by its id
     * @param id
     * @return
     */
    suspend fun deleteById(id: TId): Boolean {
        return repo().deleteById(id)
    }

    /**
     * deletes the entities
     */
    suspend fun deleteByIds(ids: List<TId>): Int {
        return repo().deleteByIds(ids)
    }

    /**
     * deletes all the items in the table
     * @return
     */
    suspend fun deleteAll(): Long {
        return repo().deleteAll()
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return repo().deleteByField(prop.name, Op.Eq, value)
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun deleteByField(prop: KProperty<*>, op: Op, value: Any): Int {
        return repo().deleteByField(prop.name, op, value)
    }

    /**
     * updates items using the query
     */
    suspend fun deleteByQuery(critera: Delete): Int {
        return repo().deleteByQuery(critera)
    }

    /**
     * updates items using the query
     */
    suspend fun delete(builder: Delete.() -> Unit): Int {
        return repo().delete(builder)
    }
}
