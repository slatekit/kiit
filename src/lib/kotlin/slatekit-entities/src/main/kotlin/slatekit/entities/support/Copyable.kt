package slatekit.entities.slatekit.entities.support

import slatekit.entities.Entity


/**
 * Supports copy operations
 */
interface Copyable<TId, out T> where TId : Comparable<TId>, T: Entity<TId> {


    /**
     * Creates a new instance of this entity
     */
    fun create(): T


    /**
     * Gets a reference to self
     */
    fun self():T


    /**
     * Creates a copy of this entity
     */
    fun copy(): T {
        val other = create()
        copy(self(), other)
        return other
    }


    /**
     * Copies the data from the source to the destination
     */
    fun copy(source:Entity<TId>, destination:Entity<TId>)


    /**
     * Copies this data to the [other] entity
     */
    fun copyTo(other: Entity<TId>?) {
        other?.let { copy(self(), other) }
    }


    /**
     * Copies the data from the [other] entity to this entity
     */
    fun copyOf(other: Entity<TId>?) {
        other?.let { copy(other, self()) }
    }
}