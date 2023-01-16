package kiit.entities.features

import java.util.*
import kiit.entities.Entity
import kiit.entities.EntityWithUUID
import kiit.entities.core.EntityOps
import kiit.query.Op

/**
 * NOTE: There is only 1 type constraint on the type parameter T to be Entity
 * ( Which is needed for all the supporting scaffolding ), so we can NOT at the moment
 * supply another type constraint on T to be EntityWithUUID to provide compile time safety.
 *
 */
interface UniqueIds<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * gets the entity from the datastore using the uuid as a string
     * @param id
     * @return
     */
    suspend fun getByUUID(id: String): T? {
        return repo().findOneByField(EntityWithUUID::uuid.name, Op.Eq, id)
    }

    /**
     * gets the entity from the datastore using the uuid
     * @param id
     * @return
     */
    suspend fun getByUUID(id: UUID): T? {
        return repo().findOneByField(EntityWithUUID::uuid.name, Op.Eq, id.toString())
    }

    /**
     * gets the entity from the datastore using the uuids
     * @param id
     * @return
     */
    suspend fun getByUUIDs(ids: List<String>): List<T>? {
        return repo().findByField(EntityWithUUID::uuid.name, Op.In, ids)
    }
}
