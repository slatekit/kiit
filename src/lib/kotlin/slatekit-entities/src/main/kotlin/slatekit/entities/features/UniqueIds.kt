package slatekit.entities.features

import java.util.*
import slatekit.entities.Entity
import slatekit.entities.EntityWithUUID
import slatekit.entities.core.ServiceSupport

/**
 * NOTE: There is only 1 type constraint on the type parameter T to be Entity
 * ( Which is needed for all the supporting scaffolding ), so we can NOT at the moment
 * supply another type constraint on T to be EntityWithUUID to provide compile time safety.
 *
 */
interface UniqueIds<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * gets the entity from the datastore using the uuid as a string
     * @param id
     * @return
     */
    fun getByUUID(id: String): T? {
        return repo().findFirstBy(EntityWithUUID::uuid.name, "=", id)
    }

    /**
     * gets the entity from the datastore using the uuid
     * @param id
     * @return
     */
    fun getByUUID(id: UUID): T? {
        return repo().findFirstBy(EntityWithUUID::uuid.name, "=", id.toString())
    }

    /**
     * gets the entity from the datastore using the uuids
     * @param id
     * @return
     */
    fun getByUUIDs(ids: List<String>): List<T>? {
        return repo().findBy(EntityWithUUID::uuid.name, "in", ids)
    }
}
