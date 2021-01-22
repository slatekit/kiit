package slatekit.data.core

interface Entity<TId : Comparable<TId>> {

    /**
     * gets the id
     *
     * NOTES:
     * 1. This is a method instead of a val "id" to allow domain entities more
     *    flexibility in how to implement their own identity.
     * 2. This also allows for 2 default implementations ( EntityWithId and EntityWithSetId )
     *
     * @return
     */
    fun identity(): TId

    /**
     * whether or not this entity is persisted.
     * @return
     */
    fun isPersisted(): Boolean
}


/**
 * interface for entities that can be updatable
 * e.g. case class copying which must be implemented in the case class
 * @tparam T
 */
interface EntityUpdatable<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    fun withId(id: TId): T

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    fun withIdAny(id: Any): T = withId(id as TId)
}
