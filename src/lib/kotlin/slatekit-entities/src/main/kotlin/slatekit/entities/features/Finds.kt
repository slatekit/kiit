package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.entities.Entity
import slatekit.entities.core.EntityOps
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.common.data.Filter
import slatekit.common.data.Logical
import slatekit.query.Query

interface Finds<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * finds items based on the field value
     * @param field: The field name
     * @param value: The value to check for
     * @return
     */
    fun findByField(field: String, value: Any): List<T> {
        return findByField(field, Op.Eq, value)
    }

    /**
     * finds items based on the field value
     * @param field: The field name
     * @param op: The comparison operator
     * @param value: The value to check for
     * @return
     */
    fun findByField(field: String, op: Op, value: Any): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(field)
        return repo().findByField(column, op, value)
    }

    /**
     * finds items based on the pairs of conditions
     * @param filters: The list of name/value pairs
     * @return
     */
    fun findByFilters(filters: List<Filter>, logical: Logical): List<T> {
        return repo().findByFilters(filters, logical)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any): List<T> {
        return findByField(prop, Op.Eq, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param op: The comparison operator
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, op: Op, value: Any): List<T> {
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
    fun findByField(prop: KProperty<*>, op: Op, value: Any, limit: Int): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        val query = Query().where(column, op, value).limit(limit)
        return repo().findByQuery(query)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findIn(prop: KProperty<*>, value: List<Any>): List<T> {
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
    fun findIn(name:String, values: List<Any>): List<T> {
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
    fun findOneByField(name: String, op: Op, value: Any): T? {
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
    fun findOneByField(prop: KProperty<*>, value: Any): T? {
        return findOneByField(prop, Op.Eq, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findOneByField(prop: KProperty<*>, op: Op, value: Any): T? {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        return repo().findOneByField(column, op, value)
    }

    /**
     * finds items based on the field value
     * @param filters: Filters to use
     * @return
     */
    fun findOneByFields(filters: List<Filter>, logical: Logical): T? {
        return repo().findByFilters(filters, logical).firstOrNull()
    }

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    fun findByQuery(query: IQuery): List<T> {
        return repo().findByQuery(query)
    }

    /**
     * finds the first item by the query
     */
    fun findOneByQuery(query: IQuery): T? {
        val results = findByQuery(query.limit(1))
        return results.firstOrNull()
    }

    fun where(prop: KProperty<*>, op: String, value: Any?): IQuery {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        return Query().where(column, op, value ?: Query.Null)
    }
}
