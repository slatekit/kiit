package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntityReads<T> : ServiceSupport<T> where T : Entity {


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
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T> {
        return entityRepo().getAll()
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
}