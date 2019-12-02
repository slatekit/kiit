package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.query.IQuery
import slatekit.query.Query
import slatekit.query.QueryEncoder

interface Finds<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    fun findByQuery(query: IQuery): List<T> {
        return repo().find(query)
    }

    /**
     * finds items based on the field value
     * @param field: The field name
     * @param value: The value to check for
     * @return
     */
    fun findByField(field: String, value: Any): List<T> {
        // Get column name from model schema ( if available )
        val column = QueryEncoder.ensureField(field)
        return repo().findByField(column, "=", value)
    }

    /**
     * finds items based on the pairs of conditions
     * @param conditions: The list of name/value pairs
     * @return
     */
    fun findByFields(conditions: List<Pair<String, Any>>): List<T> {
        // Get column name from model schema ( if available )
        val pairs = conditions.map { Pair(QueryEncoder.ensureField(it.first), it.second) }
        return repo().findByFields(pairs)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any): List<T> {
        // Get column name from model schema ( if available )
        val column = this.repo().columnName(prop)
        return repo().findByField(column, "=", value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any, limit: Int): List<T> {
        // Get column name from model schema ( if available )
        val column = this.repo().columnName(prop)
        val query = Query().where(column, "=", value).limit(limit)
        return repo().find(query)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByFieldIn(prop: KProperty<*>, value: List<Any>): List<T> {
        // Get column name from model schema ( if available )
        val column = this.repo().columnName(prop)
        return repo().findIn(column, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findFirstByField(name: String, value: Any): T? {
        // Get column name from model schema ( if available )
        val column = QueryEncoder.ensureField(name)
        return repo().findOneByField(column, "=", value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findFirstByField(prop: KProperty<*>, value: Any): T? {
        // Get column name from model schema ( if available )
        val column = this.repo().columnName(prop)
        return repo().findOneByField(column, "=", value)
    }

    /**
     * finds the first item by the query
     */
    fun findFirstByQuery(query: IQuery): T? {
        val results = findByQuery(query.limit(1))
        return results.firstOrNull()
    }

    /**
     * finds items by a stored proc
     */
    fun findByProc(name: String, args: List<Any>? = null): List<T>? {
        return repo().findByProc(name, args)
    }

    fun where(prop: KProperty<*>, op: String, value: Any?): IQuery {
        // Get column name from model schema ( if available )
        val column = this.repo().columnName(prop)
        return Query().where(column, op, value ?: Query.Null)
    }
}
