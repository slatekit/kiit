package slatekit.tracking

import slatekit.common.DateTime

/**
 * Tracks updates to an item across it's life time from creation to last updated time,
 * while storing how many times it was updated and its current value.
 * @param created: Time at which this was created
 * @param updated: Time at which this was updated
 * @param applied: Number of times this was updated/accessed
 * @param current: Current value of this time
 */
data class Writes<T>(val created: DateTime? = null,
                     val updated: DateTime? = null,
                     val applied: Long = 0,
                     val current: T? = null) {

    fun set(newValue:T?):Writes<T> {
        return this.copy(
            created = created ?: DateTime.now(),
            updated = DateTime.now(),
            applied = applied + 1,
            current = newValue)
    }

    fun map(op:(T) -> T):Writes<T> {
        return when(current) {
            null -> this
            else -> set(op(current))
        }
    }

    companion object {
        fun <T> of(item:T):Writes<T> {
            return Writes<T>(DateTime.now(), null, 0, item)
        }
    }
}
