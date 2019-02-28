package slatekit.orm.features

import slatekit.orm.core.Entity
import slatekit.orm.core.ServiceSupport

interface EntityReads<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun get(id: TId): T? {
        return repoT().get(id)
    }

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun get(ids: List<TId>): List<T> {
        return repoT().get(ids)
    }

    /**
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T> {
        return repoT().getAll()
    }

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    fun top(count: Int, desc: Boolean): List<T> {
        return repoT().top(count, desc)
    }

    /**
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? {
        return repoT().first()
    }

    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? {
        return repoT().last()
    }

    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    fun recent(count: Int): List<T> {
        return repoT().recent(count)
    }

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> {
        return repoT().oldest(count)
    }
}