package slatekit.entities.features

import slatekit.data.features.Countable
import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport

interface Counts<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * gets the total number of entities in the datastore
     * @return
     */
    fun count(): Long {
        val r = repo()
        return when (r is Countable<*, *>) {
            true -> r.count()
            false -> 0
        }
    }

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean {
        return count() > 0
    }

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()
}
