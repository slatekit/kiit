package slatekit.data.features

import slatekit.query.IQuery

interface Queryable<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T:Any {

    /**
     * updates items using the query
     */
    fun updateByQuery(query: IQuery): Int

    /**
     * deletes items using the query
     */
    fun deleteByQuery(query: IQuery): Int

    /**
     * finds items based on the query
     */
    fun findByQuery(query: IQuery): List<T> = listOf()

    /**
     * Gets the total number of records based on the query provided.
     */
    fun countByQuery(query: IQuery): Long


    fun findOneByQuery(query: IQuery): T?
}
