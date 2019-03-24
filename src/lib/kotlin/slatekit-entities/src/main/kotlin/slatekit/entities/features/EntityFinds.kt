package slatekit.entities.features

import slatekit.query.IQuery
import slatekit.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.query.QueryEncoder
import kotlin.reflect.KProperty

interface EntityFinds<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    fun find(query: IQuery): List<T> {
        return repoT().find(query)
    }

    /**
     * finds items based on the field value
     * @param field: The field name
     * @param value: The value to check for
     * @return
     */
    fun findByField(field:String, value: Any): List<T> {
        // Get column name from model schema ( if available )
        val column = QueryEncoder.ensureField(field)
        return repoT().findBy(column, "=", value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any): List<T> {
        // Get column name from model schema ( if available )
        val column = this.repoT().mapper().columnName(prop)
        return repoT().findBy(column, "=", value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any, limit:Int): List<T> {
        // Get column name from model schema ( if available )
        val column = this.repoT().mapper().columnName(prop)
        val query = Query().where(column, "=", value).limit(limit)
        return repoT().find(query)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByFieldIn(prop: KProperty<*>, value: List<Any>): List<T> {
        // Get column name from model schema ( if available )
        val column = this.repoT().mapper().columnName(prop)
        return repoT().findIn(column, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findFirstByField(prop: KProperty<*>, value: Any): T? {
        // Get column name from model schema ( if available )
        val column = this.repoT().mapper().columnName(prop)
        return repoT().findFirstBy(column, "=", value)
    }

    /**
     * finds items by a stored proc
     */
    fun findByProc(name: String, args: List<Any>? = null): List<T>? {
        return repoT().findByProc(name, args)
    }

    /**
     * finds the first item by the query
     */
    fun findFirst(query: IQuery): T? {
        val results = find(query.limit(1))
        return results.firstOrNull()
    }

    fun where(prop: KProperty<*>, op: String, value: Any?): IQuery {
        // Get column name from model schema ( if available )
        val column = this.repoT().mapper().columnName(prop)
        return Query().where(column, op, value ?: Query.Null)
    }
}