package slatekit.entities

interface IdInfo {
    fun <T> isPersisted(entity:T):Boolean
    fun <TId, T> identity(entity:T):TId
}


class EntityIdInfo : IdInfo  {
    override fun <T> isPersisted(entity:T):Boolean {
        return when(entity) {
            is Entity<*> -> entity.isPersisted()
            else -> false
        }
    }

    override fun <TId, T> identity(entity:T):TId {
        return when(entity) {
            is Entity<*> -> entity.identity() as TId
            else -> throw Exception("Unable to get identity")
        }
    }
}
