package slatekit.entities.features

import slatekit.query.IQuery
import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntityCounts<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * Gets the total number of records satisfying the query
     */
    fun count(query: IQuery):Long {
        return repoT().count(query)
    }
}