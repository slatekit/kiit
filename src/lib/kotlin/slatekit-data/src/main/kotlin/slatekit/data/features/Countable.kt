package slatekit.data.features

/**
 * Supports counting of records in the table
 */
interface Countable<TId, T> where TId : Comparable<TId> {
    /**
     * gets the total number of entities in the datastore
     * @return
     */
    fun count(): Long

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean = count() > 0

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()
}
