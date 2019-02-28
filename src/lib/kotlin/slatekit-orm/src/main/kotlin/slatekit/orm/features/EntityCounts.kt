package slatekit.orm.features

import slatekit.query.IQuery
import slatekit.orm.core.Entity
import slatekit.orm.core.ServiceSupport

interface EntityCounts<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * Gets the total number of records satisfying the query
     */
    fun count(query: IQuery):Long {
        return repoT().count(query)
    }
}