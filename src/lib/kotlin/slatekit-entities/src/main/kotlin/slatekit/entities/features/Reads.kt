package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.query.IQuery

interface Reads<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {


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
     * gets the total number of entities in the datastore
     * @return
     */
    fun count(): Long {
        return repo().count()
    }

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean {
        return repo().any()
    }

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()

    /**
     * Gets the total number of records satisfying the query
     */
    fun count(query: IQuery): Long {
        return repo().count(query)
    }

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    fun top(count: Int, desc: Boolean): List<T> {
        return repo().top(count, desc)
    }

    /**
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? {
        return repo().first()
    }

    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? {
        return repo().last()
    }

    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    fun recent(count: Int): List<T> {
        return repo().recent(count)
    }

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> {
        return repo().oldest(count)
    }
}
