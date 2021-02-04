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
        // Get column name from model schema ( if available )
        val column = columnName(field)
        return repo().findByField(column, op, value)
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
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        return repo().findByField(column, op, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findByField(prop: KProperty<*>, op: Op, value: Any, limit: Int): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        val query = repo().select().where(column, op, value).limit(limit)
        return repo().findByQuery(query)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findIn(prop: KProperty<*>, value: List<Any>): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        return repo().findIn(column, value)
    }

    /**
     * finds items based on the field value
     * @param name: The property reference
     * @param values: The value to check for
     * @return
     */
    suspend fun findIn(name:String, values: List<Any>): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(name)
        return repo().findIn(column, values)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    suspend fun findOneByField(name: String, op: Op, value: Any): T? {
        // Get column name from model schema ( if available )
        val column = columnName(name)
        return repo().findOneByField(column, op, value)
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
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        return repo().findOneByField(column, op, value)
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
    suspend fun findOneByQuery(select:Select): T? {
        val results = findByQuery(select.limit(1))
        return results.firstOrNull()
    }


    fun select(): Select = repo().select()
}
