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
