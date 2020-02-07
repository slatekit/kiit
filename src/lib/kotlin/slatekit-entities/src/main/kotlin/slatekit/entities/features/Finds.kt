package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query
import slatekit.query.QueryEncoder

interface Finds<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

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
    fun findByField(field: String, op:Op, value: Any): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(field)
        return repo().findByField(column, op, value)
    }

    /**
     * finds items based on the pairs of conditions
     * @param conditions: The list of name/value pairs
     * @return
     */
    fun findByFields(conditions: List<Pair<String, Any>>): List<T> {
        // Get column name from model schema ( if available )
        val pairs = conditions.map {
            val column = columnName(it.first)
            Pair(column, it.second)
        }
        return repo().findByFields(pairs)
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
    fun findByField(prop: KProperty<*>, op:Op, value: Any): List<T> {
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
    fun findByField(prop: KProperty<*>, op:Op, value: Any, limit: Int): List<T> {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        val query = Query().where(column, op, value).limit(limit)
        return repo().find(query)
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
    fun findOneByField(prop: KProperty<*>, op:Op, value: Any): T? {
        // Get column name from model schema ( if available )
        val column = columnName(prop.name)
        return repo().findOneByField(column, op, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findOneByFields(conditions: List<Pair<String, Any>>): T? {
        // Get column name from model schema ( if available )
        val pairs = conditions.map {
            val column = columnName(it.first)
            Pair(column, it.second)
        }
        return repo().findByFields(pairs).firstOrNull()
    }

    /**
     * finds items by a stored proc
     */
    fun findByProc(name: String, args: List<Any>? = null): List<T>? {
        return repo().findByProc(name, args)
    }

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    fun findByQuery(query: IQuery): List<T> {
        return repo().find(query)
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
