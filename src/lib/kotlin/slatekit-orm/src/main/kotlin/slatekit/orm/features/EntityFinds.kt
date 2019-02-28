package slatekit.orm.features

import slatekit.query.IQuery
import slatekit.query.Query
import slatekit.orm.core.Entity
import slatekit.orm.core.ServiceSupport
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
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any): List<T> {
        // The property could have a different column name
        val field = this.repo().mapper().model().fields.first { it.name == prop.name }
        val column = field.storedName
        return repoT().findBy(column, "=", value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findByField(prop: KProperty<*>, value: Any, limit:Int): List<T> {
        // The property could have a different column name
        val field = this.repo().mapper().model().fields.first { it.name == prop.name }
        val column = field.storedName
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
        // The property could have a different column name
        val field = this.repo().mapper().model().fields.first { it.name == prop.name }
        val column = field.storedName
        return repoT().findIn(column, value)
    }

    /**
     * finds items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun findFirstByField(prop: KProperty<*>, value: Any): T? {
        // The property could have a different column name
        val field = this.repo().mapper().model().fields.first { it.name == prop.name }
        val column = field.storedName
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
        // The property could have a different column name
        val field = this.repo().mapper().model().fields.first { it.name == prop.name }
        val column = field.storedName
        return Query().where(column, op, value ?: Query.Null)
    }
}