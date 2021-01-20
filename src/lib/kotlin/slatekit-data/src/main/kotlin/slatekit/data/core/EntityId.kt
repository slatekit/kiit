package slatekit.data.core

/**
 * Allows for custom checking of how the entity is persisted
 */
open class EntityId  {
    open fun <T> isPersisted(entity:T):Boolean {
        return when(entity) {
            is Entity<*> -> entity.isPersisted()
            else -> false
        }
    }

    open fun <TId, T> identity(entity:T):TId {
        return when(entity) {
            is Entity<*> -> entity.identity() as TId
            else -> throw Exception("Unable to get identity")
        }
    }
}
