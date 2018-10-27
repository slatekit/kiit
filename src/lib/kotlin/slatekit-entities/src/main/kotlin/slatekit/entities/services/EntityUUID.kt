package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.EntityWithUUID
import slatekit.entities.core.ServiceSupport
import java.util.*

/**
 * NOTE: There is only 1 type constraint on the type parameter T to be Entity
 * ( Which is needed for all the supporting scaffolding ), so we can NOT at the moment
 * supply another type constraint on T to be EntityWithUUID to provide compile time safety.
 *
 */
interface EntityUUID<T> : ServiceSupport<T> where T : Entity {

    /**
     * gets the entity from the datastore using the uuid as a string
     * @param id
     * @return
     */
    fun getByUUID(id: String): T? {
        return entityRepo().findFirstBy(EntityWithUUID::uuid.name, "=", id)
    }

    /**
     * gets the entity from the datastore using the uuid
     * @param id
     * @return
     */
    fun getByUUID(id: UUID): T? {
        return entityRepo().findFirstBy(EntityWithUUID::uuid.name, "=", id.toString())
    }

    /**
     * gets the entity from the datastore using the uuids
     * @param id
     * @return
     */
    fun getByUUIDs(ids: List<String>): List<T>? {
        return entityRepo().findBy(EntityWithUUID::uuid.name, "in", ids)
    }
}