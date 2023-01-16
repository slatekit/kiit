package kiit.entities.features

import kiit.data.features.Orderable
import kiit.entities.Entity
import kiit.entities.core.EntityOps
import kiit.query.Order


interface Ordered<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, TId:Number, T : Entity<TId> {

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    suspend fun top(count: Int, order: Order): List<T> {
        return performCount {  it.seq(count, order) } ?: listOf()
    }

    /**
     * Gets the first/oldest item
     * @return
     */
    suspend fun first(): T? {
        return performCount { it.first() }
    }

    /**
     * Gets the last/recent item
     * @return
     */
    suspend fun last(): T? {
        return performCount {  it.last() }
    }

    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    suspend fun recent(count: Int): List<T> {
        return performCount {  it.recent(count) } ?: listOf()
    }

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    suspend fun oldest(count: Int): List<T> {
        return performCount { it.oldest(count) } ?: listOf()
    }


    suspend fun <A> performCount(op:(Orderable<TId, T>)-> A): A? {
        val r = repo()
        return if(r is Orderable<*, *>){
            op(r as Orderable<TId, T>)
        }
        else null
    }
}
