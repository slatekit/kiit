package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.entities.Entity
import slatekit.entities.core.EntityOps
import slatekit.query.Op
import slatekit.query.Select

interface Finds<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * finds items based on the field value
     * @param field: The field name
     * @param value: The value to check for
     * @return
     */
    suspend fun findByField(field: String, value: Any): List<T> {
        return findByField(field, Op.Eq, value)
    }

    /**
     * finds items based on the field value
     * @param field: The field name
     * @param op: The comparison operator
     * @param value: The value to check for
     * @return
     */
    suspend fun findByField(field: String, op: Op, value: Any): List<T> {
        return repo().findByField(field, op, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findByField(prop: KProperty<*>, value: Any): List<T> {
        return findByField(prop, Op.Eq, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param op: The comparison operator
     * @param value: The value to check for
     * @return
     */
    suspend fun findByField(prop: KProperty<*>, op: Op, value: Any): List<T> {
        return repo().findByField(prop.name, op, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findByField(prop: KProperty<*>, op: Op, value: Any, limit: Int): List<T> {
        val query = repo().select().where(prop.name, op, value).limit(limit)
        return repo().findByQuery(query)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findIn(prop: KProperty<*>, value: List<Any>): List<T> {
        return repo().findIn(prop.name, value)
    }

    /**
     * finds items based on the field value
     * @param name: The property reference
     * @param values: The value to check for
     * @return
     */
    suspend fun findIn(name:String, values: List<Any>): List<T> {
        return repo().findIn(name, values)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findOneByField(name: String, op: Op, value: Any): T? {
        return repo().findOneByField(name, op, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findOneByField(prop: KProperty<*>, value: Any): T? {
        return findOneByField(prop, Op.Eq, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findOneByField(prop: KProperty<*>, op: Op, value: Any): T? {
        return repo().findOneByField(prop.name, op, value)
    }

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    suspend fun findByQuery(select: Select): List<T> {
        return repo().findByQuery(select)
    }

    /**
     * finds the first item by the query
     */
    suspend fun findOneByQuery(select: Select): T? {
        val results = findByQuery(select.limit(1))
        return results.firstOrNull()
    }


    suspend fun find(builder:Select.() -> Unit): List<T> = repo().find(builder)
    suspend fun findOne(builder:Select.() -> Unit): T? = repo().findOne(builder)
}
