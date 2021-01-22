package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.EntityOps


interface Ordered<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, TId:Number, T : Entity<TId> {

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
