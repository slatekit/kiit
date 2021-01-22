package slatekit.entities.slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport

interface Counts<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

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
