package kiit.entities.kiit.entities

import slatekit.common.Compatibility
import slatekit.common.data.DataAction
import slatekit.common.data.Value
import kiit.data.features.Countable
import kiit.data.features.Orderable
import kiit.entities.*
import kiit.entities.core.EntityOps
import kiit.meta.Reflector
import kiit.meta.kClass
import kiit.query.Op
import kiit.query.Order
import slatekit.results.Try
import slatekit.results.builders.Tries
import java.util.*
import kotlin.reflect.KProperty

/**
 * For Backward compatibility for some older services
 * Use instead the @see[kiit.entities.EntityServices]
 */
@Compatibility
open class EntityServiceSync<TId, T>(val repo: EntityRepo<TId, T>)
    : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, TId : Number, T : Entity<TId> {
    override fun repo(): EntityRepo<TId, T> = repo


    /**
     * ===============================================================
     * Creates
     * ===============================================================
     */
    /**
     * creates an entity in the data store without applying field data and sending events via Hooks
     * @param entity
     * @return
     */
    fun insert(entity: T): TId {
        return repo().create(entity)
    }

    /**
     * creates the entity in the data store with additional processing based on the options supplied
     * @param entity : The entity to save
     * @param options: Settings to determine whether to apply metadata, and notify via Hooks
     */
    fun create(entity: T, options: EntityOptions): Pair<TId, T> {
        // Massage
        val entityWithMeta = when (options.applyMetadata) {
            true -> applyFieldData(DataAction.Create, entity)
            false -> entity
        }

        // Create! get id
        val id = insert(entityWithMeta)

        // Update id
        val entityFinal = when (options.applyId && entityWithMeta is EntityUpdatable<*, *>) {
            true -> entityWithMeta.withIdAny(id) as T
            false -> entityWithMeta
        }
        return Pair(id, entityFinal)
    }

    /**
     * creates the entity in the data store and sends an event if there is support for Hooks
     * @param entity
     * @return
     */
    fun create(entity: T): TId {
        // Massage with timestamps
        val entityWithData = applyFieldData(DataAction.Create, entity)

        // Create! get id
        val id = repo().create(entityWithData)
        return id
    }

    /**
     * creates the entity in the data store and updates its id with the one generated
     * @param entity
     * @return
     */
    fun createWithId(entity: T): T {
        val id = create(entity)

        // Update id
        return when (entity is EntityUpdatable<*, *>) {
            true -> entity.withIdAny(id) as T
            false -> entity
        }
    }

    /**
     * creates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun createAsTry(entity: T): Try<TId> {
        return Tries.of {
            create(entity)
        }
    }

    /**
     * ===============================================================
     * Updates
     * ===============================================================
     */
    /**
     * directly modifies an entity without any additional processing/hooks/etc
     * @param entity
     * @return
     */
    fun modify(entity: T): Boolean {
        return repo().update(entity)
    }

    /**
     * directly modifies an entity without any additional processing/hooks/etc
     * @param entity
     * @return
     */
    fun patch(id:TId, values:List<Value>): Int {
        return repo().patchById(id, values)
    }

    /**
     * creates the entity in the data store with additional processing based on the options supplied
     * @param entity : The entity to save
     * @param options: Settings to determine whether to apply metadata, and notify via Hooks
     */
    fun update(entity: T, options: EntityOptions): Pair<Boolean, T> {
        // Massage
        val entityFinal = when (options.applyMetadata) {
            true -> applyFieldData(DataAction.Update, entity)
            false -> entity
        }

        // Update
        val success = modify(entityFinal)
        return Pair(success, entityFinal)
    }

    /**
     * updates the entity in the data store and sends an event if there is support for Hooks
     * @param entity
     * @return
     */
    fun update(entity: T): Boolean {
        val finalEntity = applyFieldData(DataAction.Update, entity)
        val success = repo().update(finalEntity)
        return success
    }

    /**
     * updates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun updateAsTry(entity: T): Try<Boolean> {
        return Tries.of { update(entity) }
    }

    /**
     * updates the entity field in the datastore
     * @param id: id of the entity
     * @param field: the name of the field
     * @param value: the value to set on the field
     * @return
     */
    fun update(id: TId, field: String, value: String) {
        val item = repo().getById(id)
        item?.let { entity ->
            Reflector.setFieldValue(entity.kClass, entity, field, value)
            update(entity)
        }
    }

    /**
     * updates items using the query
     */
//    fun updateByQuery(query: IQuery): Int {
//        return repo().patchByQuery(query)
//    }

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun patchByField(prop: KProperty<*>, value: Any): Int {
        val column = columnName(prop.name)
        return repo().patchByField(column, value)
    }

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
//    fun patchByFields(prop: KProperty<*>, oldValue: Any?, newValue:Any?): Int {
//        val column = columnName(prop.name)
//        return repo().patchByQuery(Query().set(column, oldValue).where(column, Op.Eq, newValue))
//    }

    /**
     * ===============================================================
     * Saves
     * ===============================================================
     */
    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?): Try<TId> {
        val result = try {
            entity?.let { item ->
                val saveResult = when (item.isPersisted()) {
                    false -> {
                        val finalEntity = applyFieldData(DataAction.Create, item)
                        val id = repo().create(finalEntity)
                        if (isCreated(id)) Tries.success(id) else Tries.errored("Error creating item")
                    }
                    true -> {
                        val finalEntity = applyFieldData(DataAction.Update, item)
                        val updated = repo().update(finalEntity)
                        if (updated) Tries.success(finalEntity.identity()) else Tries.errored("Error updating item")
                    }
                }
                saveResult
            } ?: Tries.errored("Entity not provided")
        } catch (ex: Exception) {
            Tries.errored<TId>(ex)
        }
        return result
    }

    /**
     * saves all the entities
     *
     * @param items
     */
    fun saveAll(items: List<T>):List<Pair<TId?, Boolean>> {
        return repo().saveAll(items)
    }

    /**
     * ===============================================================
     * Gets
     * ===============================================================
     */
    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun getById(id: TId): T? {
        return repo().getById(id)
    }

    /**
     * gets the entity from the datastore using the id
     * @param ids
     * @return
     */
    fun getByIds(ids: List<TId>): List<T> {
        return repo().getByIds(ids)
    }

    /**
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T> {
        return repo().getAll()
    }

    /**
     * gets the entity from the datastore using the uuid as a string
     * @param id
     * @return
     */
    fun getByUUID(id: String): T? {
        return repo().findOneByField(EntityWithUUID::uuid.name, Op.Eq, id)
    }

    /**
     * gets the entity from the datastore using the uuid
     * @param id
     * @return
     */
    fun getByUUID(id: UUID): T? {
        return repo().findOneByField(EntityWithUUID::uuid.name, Op.Eq, id.toString())
    }

    /**
     * gets the entity from the datastore using the uuids
     * @param id
     * @return
     */
    fun getByUUIDs(ids: List<String>): List<T>? {
        return repo().findByField(EntityWithUUID::uuid.name, Op.In, ids)
    }

    /**
     * ===============================================================
     * FINDS
     * ===============================================================
     */
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
//    fun findByField(prop: KProperty<*>, op: Op, value: Any, limit: Int): List<T> {
//        // Get column name from model schema ( if available )
//        val column = columnName(prop.name)
//        val query = Query().where(column, op, value).limit(limit)
//        return repo().findByQuery(query)
//    }

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
     * finds items based on the query
     * @param query
     * @return
     */
//    fun findByQuery(query: IQuery): List<T> {
//        return repo().findByQuery(query)
//    }

    /**
     * finds the first item by the query
     */
//    fun findOneByQuery(query: IQuery): T? {
//        val results = findByQuery(query.limit(1))
//        return results.firstOrNull()
//    }

//    fun where(prop: KProperty<*>, op: String, value: Any?): IQuery {
//        // Get column name from model schema ( if available )
//        val column = columnName(prop.name)
//        return Query().where(column, op, value ?: Query.Null)
//    }

    /**
     * ===============================================================
     * Deletes
     * ===============================================================
     */
    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean {
        return repo().delete(entity)
    }

    /**
     * updates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun deleteAsTry(entity: T): Try<Boolean> {
        return Tries.of { delete(entity) }
    }

    /**
     * deletes the entity by its id
     * @param id
     * @return
     */
    fun deleteById(id: TId): Boolean {
        return repo().deleteById(id)
    }

    /**
     * deletes the entities
     */
    fun deleteByIds(ids: List<TId>): Int {
        return repo().deleteByIds(ids)
    }

    /**
     * deletes all the items in the table
     * @return
     */
    fun deleteAll(): Long {
        return repo().deleteAll()
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return repo().deleteByField(prop.name, Op.Eq, value)
    }

    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, op: Op, value: Any): Int {
        return repo().deleteByField(prop.name, op, value)
    }

    /**
     * updates items using the query
     */
//    fun deleteByQuery(query: IQuery): Int {
//        return repo().deleteByQuery(query)
//    }

    /**
     * ===============================================================
     * Counts
     * ===============================================================
     */
    /**
     * gets the total number of entities in the datastore
     * @return
     */
    fun count(): Long {
        val r = repo()
        return when (r is Countable<*, *>) {
            true -> r.count()
            false -> 0
        }
    }

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean {
        return count() > 0
    }

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()

    /**
     * ===============================================================
     * Ordered
     * ===============================================================
     */
    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param order : Whether to sort by descending
     * @return
     */
    fun top(count: Int, order: Order): List<T> {
        return performCount {  it.seq(count, order) } ?: listOf()
    }

    /**
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? {
        return performCount { it.first() }
    }

    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? {
        return performCount {  it.last() }
    }

    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    fun recent(count: Int): List<T> {
        return performCount {  it.recent(count) } ?: listOf()
    }

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> {
        return performCount { it.oldest(count) } ?: listOf()
    }


    fun <A> performCount(op:(Orderable<TId, T>)-> A): A? {
        val r = repo()
        return if(r is Orderable<*, *>){
            op(r as Orderable<TId, T>)
        }
        else null
    }

    /**
     * ===============================================================
     * Upserts
     * ===============================================================
     */
    /**
     * Upserts an item by checking if it exists already using it identity
     */
    fun upsertById(item: T, uuid: String): Try<T> {
        return upsertById(item, uuid, { newItem -> this.create(newItem) }, { oldItem -> this.update(oldItem) })
    }


    /**
     * Upserts an item by checking if it exists already using it identity and calls the create or update
     * operations supplied. If they are null, defaults to using the existing create/update methods
     */
    fun upsertById(item: T, uuid: String, createOp:((T) -> TId)? = null, updateOp:((T) -> Boolean)? = null): Try<T> {
        return try {
            when (item.isPersisted()) {
                false -> {
                    val id = createOp?.let { it(item) }  ?: this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                true -> {
                    val updated = updateOp?.let { it(item) } ?: this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch( ex:Exception ) {
            Tries.unexpected(ex)
        }
    }


    /**
     * Upserts an item by checking if it exists already using it uuid
     */
    fun upsertByUUID(item: T, uuid: String): Try<T> {
        return upsertByUUID(item, uuid, { newItem -> this.create(newItem) }, { oldItem -> this.update(oldItem) })
    }


    /**
     * Upserts an item by checking if it exists already using it uuid and calls the create or update
     * operations supplied. If they are null, defaults to using the existing create/update methods
     */
    fun upsertByUUID(item: T, uuid: String, createOp:((T) -> TId)? = null, updateOp:((T) -> Boolean)? = null): Try<T> {
        return try {
            val existing: T? = this.getByUUID(uuid)
            when (existing) {
                null -> {
                    val id = createOp?.let { it(item) }  ?: this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                else -> {
                    val updated = updateOp?.let { it(item) } ?: this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch (ex:Exception) {
            Tries.unexpected(ex)
        }
    }


    /**
     * Creates an item only if it does NOT exist already by checking its uuid
     */
    fun createByUUID(item: T, uuid: String, createOp:((T) -> TId)? = null): Try<T> {
        return try {
            val existing: T? = this.getByUUID(uuid)
            when (existing) {
                null -> {
                    val id = createOp?.let { it(item) }  ?: this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                else -> {
                    Tries.success(item)
                }
            }
        } catch (ex:Exception) {
            Tries.unexpected(ex)
        }
    }
}
