/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.entities

import kotlin.reflect.KProperty
import slatekit.common.DateTime
import slatekit.entities.Consts.idCol
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.EntityStore
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query
import slatekit.query.where

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * NOTE: This is basically a GenericRepository implementation
 * @tparam T
 */
interface Repo<TId, T> : EntityStore where TId : Comparable<TId> {

    val info: EntityInfo

    /**
     * the name of the id field.
     * @return
     */
    fun id(): String = info.model?.idField?.name ?: idCol

    /**
     * The name of the table in the datastore
     */
    override fun name(): String = info.name()

    /**
     * creates the entity in the data store
     * @param entity
     * @return
     */
    fun create(entity: T): TId

    /**
     * ======================================================================================================
     * UPDATES: All update methods
     * ======================================================================================================
     */

    /**
     * updates the entity in the datastore
     * @param entity
     * @return
     */
    fun update(entity: T): Boolean

    /**
     * updates items based on the field name
     * @param field: The property reference
     * @param value: The value to set
     * @return
     */
    fun updateByField(field: String, value: Any): Int

    /**
     * updates items using the proc and args
     */
    fun updateByProc(name: String, args: List<Any>? = null): Int

    /**
     * updates items using the query
     */
    fun updateByQuery(query: IQuery): Int

    /**
     * ======================================================================================================
     * DELETES: All delete methods
     * ======================================================================================================
     */

    /**
     * deletes the entity in memory
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean =
        entity?.let { item -> deleteById(identity(item)) } ?: false

    /**
     * deletes the entity by id
     * @param id
     * @return
     */
    fun deleteById(id: TId): Boolean

    /**
     * deletes all entities from the data store using the ids
     * @param ids
     * @return
     */
    fun deleteByIds(ids: List<TId>): Int

    /**
     * deletes all entities from the data store using the ids
     * @param ids
     * @return
     */
    fun deleteAll(): Long

    /**
     * deletes items based on the field name and value
     * @param field: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(field: String, op: Op, value: Any): Int

    /**
     * deletes items using the query
     */
    fun deleteByQuery(query: IQuery): Int

    /**
     * ======================================================================================================
     * GETS: All get methods
     * ======================================================================================================
     */

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    @Deprecated("Replaced with getById", replaceWith = ReplaceWith("getById"))
    fun get(id: TId): T? = getById(id)

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun getById(id: TId): T?

    /**
     * gets the entity from the datastore using the id
     * @param ids
     * @return
     */
    @Deprecated("Replaced with getByIds", replaceWith = ReplaceWith("getByIds"))
    fun get(ids: List<TId>): List<T> = getByIds(ids)

    /**
     * gets the entity from the datastore using the id
     * @param ids
     * @return
     */
    fun getByIds(ids: List<TId>): List<T>

    /**
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T>

    /**
     * ======================================================================================================
     * SAVES: All save methods
     * ======================================================================================================
     */

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?) {
        entity?.let { item ->
            if (isPersisted(item))
                update(item)
            else
                create(item)
        }
    }

    /**
     * saves all the entities
     *
     * @param items
     */
    fun saveAll(items: List<T>) = items.forEach { item -> save(item) }

    /**
     * ======================================================================================================
     * FIND: All find methods
     * ======================================================================================================
     */

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    fun find(query: IQuery): List<T> = listOf()

    /**
     * finds items based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findBy(field: String, op: String, value: Any): List<T> = listOf()

    /**
     * finds items based on the conditions
     */
    fun findByFields(conditions: List<Pair<String, Any>>): List<T> = listOf()

    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    fun findIn(field: String, value: List<Any>): List<T> = listOf()

    /**
     * finds first item based on the query
     * @param query: name of field
     * @return
     */
    fun findFirst(query: IQuery): T? = null

    /**
     * finds first item based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findFirstBy(field: String, op: String, value: Any): T? = null

    /**
     * finds items by using the sql
     * @param name: Name of the stored proc
     * @param args: Arguments to the proc
     * @return
     */
    fun findByProc(name: String, args: List<Any>?): List<T>? = listOf()

    /**
     * ======================================================================================================
     * LOOKUP: All "lookup" methods e.g. first, last, recent, oldess, count, etc
     * ======================================================================================================
     */

    /**
     * Gets the total number of records based on the query provided.
     */
    fun count(query: IQuery): Long

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    fun top(count: Int, desc: Boolean): List<T>

    /**
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? = takeFirst { oldest(1) }

    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? = takeFirst { recent(1) }

    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    fun recent(count: Int): List<T> = top(count, true)

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> = top(count, false)

    /**
     * takes the
     * @param call
     * @return
     */
    fun takeFirst(call: () -> List<T>): T? = call().firstOrNull()

    /**
     * Return a query builder for more complex searches
     */
    fun query(): Query = info.queryBuilder?.invoke() ?: Query()

    /**
     * Gets the column name for the Kproperty from the model schema if available
     * or defaults to the property name.
     */
    fun columnName(prop: KProperty<*>): String {
        val model = info.model
        return when (model) {
            null -> prop.name
            else -> if (model.any) model.fields.first { it.name == prop.name }.storedName else prop.name
        }
    }

    /**
     * Purges data older than the number of days supplied
     */
    fun <ETimed> purge(days: Int): Int where ETimed : EntityWithTime {
        val since = DateTime.now().plusDays((days * -1).toLong())
        val count = deleteByQuery(Query().where(EntityWithTime::createdAt, "<", since))
        return count
    }

    fun isPersisted(entity:T):Boolean {
        return info.idInfo.isPersisted(entity)
    }

    fun identity(entity:T):TId {
        return info.idInfo.identity(entity)
    }
}
