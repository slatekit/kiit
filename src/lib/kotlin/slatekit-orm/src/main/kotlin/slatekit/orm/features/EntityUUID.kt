package slatekit.orm.features

import slatekit.orm.core.Entity
import slatekit.orm.core.EntityWithUUID
import slatekit.orm.core.ServiceSupport
import java.util.*

/**
 * NOTE: There is only 1 type constraint on the type parameter T to be Entity
 * ( Which is needed for all the supporting scaffolding ), so we can NOT at the moment
 * supply another type constraint on T to be EntityWithUUID to provide compile time safety.
 *
 */
interface EntityUUID<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * gets the entity from the datastore using the uuid as a string
     * @param id
     * @return
     */
    fun getByUUID(id: String): T? {
        return repoT().findFirstBy(EntityWithUUID::uuid.name, "=", id)
    }

    /**
     * gets the entity from the datastore using the uuid
     * @param id
     * @return
     */
    fun getByUUID(id: UUID): T? {
        return repoT().findFirstBy(EntityWithUUID::uuid.name, "=", id.toString())
    }

    /**
     * gets the entity from the datastore using the uuids
     * @param id
     * @return
     */
    fun getByUUIDs(ids: List<String>): List<T>? {
        return repoT().findBy(EntityWithUUID::uuid.name, "in", ids)
    }
}