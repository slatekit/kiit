package slatekit.entities.services

import slatekit.entities.Entity
import slatekit.entities.core.IEntityService

/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
interface EntityServices<TId, T> : IEntityService,
        EntityRecord<TId, T> where TId:Comparable<TId>, T : Entity<TId> {

    /**
     * gets the total number of entities in the datastore
     * @return
     */
    override fun count(): Long {
        return repoT().count()
    }

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    override fun any(): Boolean {
        return repoT().any()
    }

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()
}
