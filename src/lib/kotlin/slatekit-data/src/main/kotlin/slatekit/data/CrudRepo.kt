package slatekit.data


interface CrudRepo<TId, T> : Repo<TId, T> where TId : Comparable<TId>, T:Any {
    /**
     * creates the entity in the data store
     * @param entity
     * @return
     */
    fun create(entity: T): TId


    /**
     * updates the entity in the datastore
     * @param entity
     * @return
     */
    fun update(entity: T): Boolean


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
     * Saves all the items using existing save method.
     * NOTE: This does NOT do a batch insert.
     * A separate method will be provided for batch inserts
     */
    fun saveAll(items: List<T>) = items.forEach { item -> save(item) }


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
    fun getByIds(ids: List<TId>): List<T>


    /**
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T>


    /**
     * deletes the entity in memory
     * @param entity
     */
    fun delete(entity: T?): Boolean


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
}
