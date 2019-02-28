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

package slatekit.orm.core

import slatekit.common.naming.Namer
import slatekit.common.encrypt.Encryptor
import slatekit.meta.models.Model
import slatekit.query.IQuery
import slatekit.query.Query
import slatekit.orm.Consts.idCol
import slatekit.orm.databases.vendors.MySqlConverter
import slatekit.meta.models.ModelMapper
import kotlin.reflect.KClass

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * NOTE: This is basically a GenericRepository implementation
 * @param entityType : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable : The name of the table ( defaults to entity name )
 * @tparam T
 */
abstract class EntityRepo<TId, T>(
    entityType: KClass<*>,
    entityIdType: KClass<*>? = null,
    entityMapper: EntityMapper? = null,
    nameOfTable: String? = null,
    encryptor: Encryptor? = null,
    val namer: Namer? = null,
    val encodedChar: Char = '`',
    val queryBuilder:(() -> Query)? = null
)
    : IEntityRepo where TId:Comparable<TId>, T : Entity<TId> {

    protected val _nameOfTable = nameOfTable
    protected val _entityType: KClass<*> = entityType
    protected val _entityIdType: KClass<*> = entityIdType ?: Long::class
    protected val _entityModel: Model = entityMapper?.model() ?: ModelMapper.loadSchema(entityType, namer = namer)
    protected val _entityMapper: EntityMapper = entityMapper ?: EntityMapper(_entityModel, MySqlConverter, encryptor = encryptor)

    /**
     * The name of the table in the datastore
     */
    override fun repoName(): String {
        val rawName = _nameOfTable ?: _entityType.simpleName ?: ""
        val finalName = namer?.rename(rawName) ?: rawName[0].toLowerCase() + rawName.substring(1)
        return finalName
    }

    /**
     * gets the internal mapper used to convert entities to sql or records to entity
     * @return
     */
    override fun mapper(): EntityMapper = _entityMapper

    /**
     * the name of the id field.
     * @return
     */
    fun idName(): String = _entityModel.idField?.name ?: idCol

    /**
     * creates the entity in the datastore
     * @param entity
     * @return
     */
    abstract fun create(entity: T): TId

    /**
     * updates the entity in the datastore
     * @param entity
     * @return
     */
    abstract fun update(entity: T): T

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to set
     * @return
     */
    abstract fun updateByField(field: String, value: Any): Int

    /**
     * updates items using the proc and args
     */
    abstract fun updateByProc(name: String, args: List<Any>? = null): Int

    /**
     * updates items using the query
     */
    abstract fun updateByQuery(query: IQuery): Int

    /**
     * deletes the entity by id
     * @param id
     * @return
     */
    abstract fun delete(id: TId): Boolean

    /**
     * deletes all entities from the datastore using the ids
     * @param ids
     * @return
     */
    abstract fun delete(ids: List<TId>): Int

    /**
     * deletes the entity in memory
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean =
            entity?.let { item -> delete(item.identity()) } ?: false

    /**
     * deletes items based on the field name and value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    abstract fun deleteByField(field: String, value: Any): Int

    /**
     * deletes items using the query
     */
    abstract fun deleteByQuery(query: IQuery): Int

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    abstract fun get(id: TId): T?

    /**
     * gets the entity from the datastore using the id
     * @param ids
     * @return
     */
    abstract fun get(ids: List<TId>): List<T>

    /**
     * gets all the entities from the datastore.
     * @return
     */
    abstract fun getAll(): List<T>

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    abstract fun top(count: Int, desc: Boolean): List<T>


    /**
     * Gets the total number of records based on the query provided.
     */
    abstract fun count(query: IQuery):Long


    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean = count() > 0

    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?) {
        entity?.let { item ->
            if (item.isPersisted())
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
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? = takeFirst({ -> oldest(1) })

    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? = takeFirst({ -> recent(1) })

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
    open fun query(): Query = queryBuilder?.invoke() ?: Query()


    /**
     * finds items based on the query
     * @param query
     * @return
     */
    open fun find(query: IQuery): List<T> = listOf()

    /**
     * finds items based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    open fun findBy(field: String, op: String, value: Any): List<T> = listOf()

    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    open fun findIn(field: String, value: List<Any>): List<T> = listOf()

    /**
     * finds first item based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    open fun findFirstBy(field: String, op: String, value: Any): T? = null

    /**
     * finds items by using the sql
     * @param query
     * @return
     */
    open fun findByProc(name: String, args: List<Any>?): List<T>? = listOf()

    /**
     * Hook for derived classes to handle additional logic before saving
     * @param entity
     */
    protected fun onBeforeSave(entity: T) = {}
}
