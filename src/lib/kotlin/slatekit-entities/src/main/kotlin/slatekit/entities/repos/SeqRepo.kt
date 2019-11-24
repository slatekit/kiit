package slatekit.entities.repos

/**
 * Repo that has a sequential / serial Id. E.g. Numeric ( int, long )
 */
interface SeqRepo<TId, T> where TId : Comparable<TId>, TId: Number {

    /**
     * gets the top count entities in the datastore sorted by asc order
     * @param count: Top / Limit count of entities
     * @param desc : Whether to sort by descending
     * @return
     */
    fun top(count: Int, desc: Boolean): List<T>

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
    fun recent(count: Int): List<T> = top(count, true)

    /**
     * Gets the most oldest n items represented by count
     * @param count
     * @return
     */
    fun oldest(count: Int): List<T> = top(count, false)

    /**
     * takes the
     * @param call
     * @return
     */
    fun takeFirst(call: () -> List<T>): T? = call().firstOrNull()
}
