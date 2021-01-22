package slatekit.data


/**
 * Repository
 */
interface Repo<TId, T> : Table<TId> where TId : Comparable<TId> {
    /**
     * Check if entity is persisted ( has an id ) in the database
     */
    fun isPersisted(entity: T): Boolean

    /**
     * Get identity of entity
     */
    fun identity(entity: T): TId
}
