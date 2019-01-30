package slatekit.entities.services

import slatekit.common.query.IQuery
import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntityCounts<T> : ServiceSupport<T> where T : Entity {

    /**
     * Gets the total number of records satisfying the query
     */
    fun count(query:IQuery):Long {
        return entityRepo().count(query)
    }
}