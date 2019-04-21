package slatekit.entities.services

import slatekit.entities.Entities
import slatekit.entities.Entity
import slatekit.entities.EntityRepo
import slatekit.entities.core.IEntityRepo
import slatekit.entities.core.IEntityService
import slatekit.entities.core.ServiceSupport


/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
interface EntitySupport<TId, T> : IEntityService,
        ServiceSupport<TId, T> where TId:Comparable<TId>, T : Entity<TId> {

    val entities:Entities
    val repo:EntityRepo<TId, T>


    //override fun entities(): Entities = entities

    override fun repo(): IEntityRepo = repo

    override fun repoT(): EntityRepo<TId, T> = repo


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
