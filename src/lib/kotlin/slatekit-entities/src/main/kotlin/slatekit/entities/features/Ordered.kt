package slatekit.entities.features

import slatekit.data.features.Orderable
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
        return performCount {  it.seq(count, desc) } ?: listOf()
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
}
