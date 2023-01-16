package kiit.data

import kiit.data.core.PKey
import kiit.data.features.Inspectable


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
     * Check if entity id is a valid persisted value
     */
    fun isPersisted(id:TId): Boolean = meta.id.isPersisted(id)


    /**
     * Get identity of entity - for convenience
     */
    fun identity(model: T): TId = meta.id.identity(model)
}
