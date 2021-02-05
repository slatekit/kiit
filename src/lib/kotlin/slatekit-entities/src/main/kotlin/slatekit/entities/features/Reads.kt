package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.EntityOps

interface Reads<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * gets the entity from the datastore using the id
     * @param id
     * @return
     */
    suspend fun getById(id: TId): T? {
        return repo().getById(id)
    }

    /**
     * gets the entity from the datastore using the id
     * @param ids
     * @return
     */
    suspend fun getByIds(ids: List<TId>): List<T> {
        return repo().getByIds(ids)
    }

    /**
     * gets all the entities from the datastore.
     * @return
     */
    suspend fun getAll(): List<T> {
        return repo().getAll()
    }
}
