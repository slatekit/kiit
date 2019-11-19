package slatekit.entities.support

import slatekit.entities.Entities
import slatekit.entities.Entity
import slatekit.entities.Repo
import slatekit.entities.core.EntityStore
import slatekit.entities.core.GenericService
import slatekit.entities.core.ServiceSupport

/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
interface EntitySupport<TId, T> : GenericService,
        ServiceSupport<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    val entities: Entities
    val repo: Repo<TId, T>

    override fun store(): EntityStore = repo

    override fun repo(): Repo<TId, T> = repo

    /**
     * gets the total number of entities in the datastore
     * @return
     */
    fun count(): Long {
        return repo().count()
    }

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean {
        return repo().any()
    }

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()
}
