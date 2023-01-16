package kiit.entities.features

import kiit.data.features.Countable
import kiit.entities.Entity
import kiit.entities.core.EntityOps
import kiit.query.Delete
import kiit.query.Select

interface Counts<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * gets the total number of entities in the datastore
     * @return
     */
    suspend fun count(): Long {
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
    suspend fun any(): Boolean {
        return count() > 0
    }

    /**
     * whether this is an empty dataset
     */
    suspend fun isEmpty(): Boolean = !any()



    /**
     * updates items using the query
     */
    suspend fun count(builder: Select.() -> Unit): Double {
        return repo().count(builder)
    }
}
