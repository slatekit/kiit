package slatekit.entities.core

import slatekit.common.query.IQuery
import slatekit.common.query.Query
import slatekit.meta.Reflector
import slatekit.meta.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
open interface EntityServices<T> : IEntityService where T : Entity {

    fun entityRepo(): EntityRepo<T>
    fun entities(): Entities


    /**
     * creates the entity in the datastore
     * @param entity
     * @return
     */
    fun create(entity: T): Long {
        val finalEntity = applyFieldData(1, entity)
        return entityRepo().create(finalEntity)
    }


    /**
     * updates the entity in the datastore
     * @param entity
     * @return
     */
    fun update(entity: T): T {
        val finalEntity = applyFieldData(2, entity)
        return entityRepo().update(finalEntity)
    }


    /**
     * updates the entity field in the datastore
     * @param id: id of the entity
     * @param field: the name of the field
     * @param value: the value to set on the field
     * @return
     */
    fun update(id: Long, field: String, value: String): Unit {
        val item = get(id)
        item?.let { entity ->
            Reflector.setFieldValue(entity.kClass, entity, field, value)
            update(entity)
        }
    }


    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun updateByField(prop: KProperty<*>, value: Any): Int {
        return entityRepo().updateByField(prop.name, value)
    }


    /**
     * updates items by a stored proc
     */
    fun updateByProc(name:String, args:List<Any>? = null): Int {
        return entityRepo().updateByProc(name, args)
    }


    /**
     * updates items using the query
     */
    fun updateByQuery(query: IQuery): Int {
        return entityRepo().updateByQuery(query)
    }


    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean {
        return entityRepo().delete(entity)
    }


    /**
     * deletes the entity by its id
     * @param id
     * @return
     */
    fun deleteById(id: Long): Boolean = entityRepo().delete(id)


    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return entityRepo().deleteByField(prop.name, value)
    }


    /**
     * updates items using the query
     */
    fun deleteByQuery(query: IQuery): Int {
        return entityRepo().deleteByQuery(query)
    }


    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun get(id: Long): T? {
        return entityRepo().get(id)
    }


    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun get(ids: List<Long>): List<T> {
        return entityRepo().get(ids)
    }


    /**
     * Gets a relation model associated w the current model by the property supplied.
     * E.g. get the membership and user associated with the membership: 1 membership = 1 user
     * @param id    : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop  : the field to check on current model for the foreign key id
     * @sample member.getRelation[User](1, Member::userId, User::class)
     */
    fun <R> getRelation(id: Long, prop:KProperty<*>, model: KClass<*>): R? where R : Entity{

        TODO.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity =  entityRepo().get(id)
        return entity?.let { ent ->
            val id = prop.getter.call(entity) as Long
            val relRepo = entities().getRepo<R>(model)
            val rel = relRepo.get(id)
            rel
        }
    }


    /**
     * Gets a relational model associated w the current model by the property supplied.
     * E.g. get the membership and user associated with the membership: 1 membership = 1 user
     * @param id    : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop  : the field to check on current model for the foreign key id
     * @sample member.getRelation[User](1, Member::userId, User::class)
     */
    fun <R> getWithRelation(id: Long, prop:KProperty<*>, model: KClass<*>): Pair<T?,R?> where R : Entity{

        TODO.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity =  entityRepo().get(id)
        return entity?.let { ent ->
            val id = prop.getter.call(entity) as Long
            val relRepo = entities().getRepo<R>(model)
            val rel = relRepo.get(id)
            Pair(entity, rel)
        } ?: Pair(null, null)
    }


    /**
     * Gets all the models associated w the current model by the property supplied.
     * E.g. get all the members in a group: 1 group = many members
     * @param id    : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop  : the field to check on current model for the foreign key id
     * @sample group.getWithRelations[Member](1, Member::class, Member::groupId)
     */
    fun <R> getWithRelations(id: Long, model: KClass<*>, prop:KProperty<*>): Pair<T?,List<R>> where R : Entity{

        TODO.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity =  entityRepo().get(id)
        return entity?.let { ent ->
            val relRepo = entities().getRepo<R>(model)
            val relations = relRepo.findBy(prop.name, "=", id)
            Pair(entity, relations)
        } ?: Pair(null, listOf())
    }


    /**
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T> {
        return entityRepo().getAll()
    }


    /**
     * gets the total number of entities in the datastore
     * @return
     */
    override fun count(): Long {
        return entityRepo().count()
    }


    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    fun top(count: Int, desc: Boolean): List<T> {
        return entityRepo().top(count, desc)
    }


    /**
     * determines if there are any entities in the datastore
     * @return
     */
    override fun any(): Boolean {
        return entityRepo().any()
    }


    /**
     * whether this is an empty dataset
     */
    fun isEmpty():Boolean = !any()


    /**
     * saves an entity by either creating it or updating it based on
     * checking its persisted flag.
     * @param entity
     */
    fun save(entity: T?): Unit {
        entity?.let { item ->
            val finalEntity = applyFieldData(3, item)
            entityRepo().save(finalEntity)
        }
    }


    /**
     * saves all the entities
     *
     * @param items
     */
    fun saveAll(items: List<T>): Unit {
        entityRepo().saveAll(items)
    }


    /**
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? {
        return entityRepo().first()
    }


    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? {
        return entityRepo().last()
    }


    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    fun recent(count: Int): List<T> {
        return entityRepo().recent(count)
    }


    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> {
        return entityRepo().oldest(count)
    }


    /**
     * finds items based on the query
     * @param query
     * @return
     */
    fun find(query: IQuery): List<T> {
        return entityRepo().find(query)
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
        return entityRepo().findBy(column, "=", value)
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
        return entityRepo().findFirstBy(column, "=", value)
    }


    /**
     * finds items by a stored proc
     */
    fun findByProc(name:String, args:List<Any>? = null):List<T>? {
        return entityRepo().findByProc(name, args)
    }


    /**
     * finds the first item by the query
     */
    fun findFirst(query: IQuery): T? {
        val results = find(query.limit(1))
        return results.firstOrNull()
    }


    /**
     * Hook for derived to apply any other logic/field changes before create/update
     * @param mode
     * @param entity
     * @return
     */
    fun applyFieldData(mode: Int, entity: T): T {
        return entity
    }


    open fun where(prop: KProperty<*>, op:String, value:Any): IQuery {
        // The property could have a different column name
        val field = this.repo().mapper().model().fields.first { it.name == prop.name }
        val column = field.storedName
        return Query().where(column, op, value)
    }
}
