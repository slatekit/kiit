package slatekit.data

import slatekit.data.core.PKey
import slatekit.data.features.Inspectable


/**
 * Repository interface, simply contains info about its id and table
 * @see[slatekit.data.core.Meta] for more info.
 * NOTE: This does NOT use Reflection
 */
interface Repo<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T:Any {
    /**
     * Name of the table for convenience
     */
    val name: String get() { return meta.name }


    /**
     * Name of the Id/Primary key for convenience
     */
    val pkey: PKey get() { return meta.pkey }


    /**
     * Check if entity is persisted ( has an id ) in the database - for convenience
     */
    fun isPersisted(model: T): Boolean = meta.id.isPersisted(model)


    /**
     * Get identity of entity - for convenience
     */
    fun identity(model: T): TId = meta.id.identity(model)
}
