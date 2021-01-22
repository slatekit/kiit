package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.query.IQuery

interface Reads<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    fun getById(id: TId): T? {
        return repo().getById(id)
    }

    /**
     * gets the entity from the datastore using the id
     * @param ids
     * @return
     */
    fun getByIds(ids: List<TId>): List<T> {
        return repo().getByIds(ids)
    }

    /**
     * gets all the entities from the datastore.
     * @return
     */
    fun getAll(): List<T> {
        return repo().getAll()
    }
}
