package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.IEntityService


/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
open interface EntityServices<T> : IEntityService,
        EntityFeatureAll<T> where T : Entity {

    /**
     * gets the total number of entities in the datastore
     * @return
     */
    override fun count(): Long {
        return entityRepo().count()
    }


    /**
     * determines if there are any entities in the datastore
     * @return
     */
    override fun any(): Boolean {
        return entityRepo().any()
    }


    /**
     * whether this is an empty dataset
     */
    fun isEmpty():Boolean = !any()
}
