package slatekit.tracking

import slatekit.common.DateTime

/**
 * Tracks an nullable item across it's LifeTime from creation to last updated time,
 * while storing how many times it was updated and its current value.
 * @param created: Time at which this was created
 * @param updated: Time at which this was updated
 * @param applied: Number of times this was updated/accessed
 * @param current: Current value of this time
 */
data class LifeTime<T>(val created: DateTime?,
                       val updated: DateTime?,
                       val applied: Long,
                       val current: T?) {

    fun update(newValue:T?):LifeTime<T> {
        return this.copy(
            created = created ?: DateTime.now(),
            updated = DateTime.now(),
            applied = applied + 1,
            current = newValue)
    }
}
