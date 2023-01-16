package kiit.data.features

import kiit.query.Order

/**
 * Supports access to records that are ordered sequential / serial Id. E.g. Numeric ( int, long )
 */
interface Orderable<TId, T> where TId : Comparable<TId> {

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    fun seq(count: Int, order:Order): List<T>

    /**
     * Gets the first/oldest item
     * @return
     */
    fun first(): T? = takeFirst { oldest(1) }

    /**
     * Gets the last/recent item
     * @return
     */
    fun last(): T? = takeFirst { recent(1) }

    /**
     * Gets the most recent n items represented by count
     * @param count
     * @return
     */
    fun recent(count: Int): List<T> = seq(count, Order.Dsc)

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> = seq(count, Order.Asc)

    /**
     * takes the
     * @param call
     * @return
     */
    fun takeFirst(call: () -> List<T>): T? = call().firstOrNull()
}
