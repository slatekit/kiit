package slatekit.entities.core

import slatekit.common.ext.toUUId
import slatekit.entities.Entity
import java.util.*
import kotlin.reflect.KClass

interface IdInfo {
    fun <T> isPersisted(entity:T):Boolean
    fun <TId, T> identity(entity:T):TId
    fun <TId> convertToId(id: String, entityType: KClass<*>): TId
}


open class EntityIdInfo : IdInfo {
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

    @Suppress("UNCHECKED_CAST")
    override fun <TId> convertToId(id: String, entityType: KClass<*>): TId {
        return when (entityType) {
            Int::class -> id.toInt() as TId
            Long::class -> id.toLong() as TId
            UUID::class -> id.toUUId() as TId
            else -> id as TId
        }
    }
}
