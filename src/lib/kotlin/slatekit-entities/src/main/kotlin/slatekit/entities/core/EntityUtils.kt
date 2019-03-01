package slatekit.entities.core

import slatekit.common.toUUId
import java.util.*
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
fun <TId> convertToId(id:String, entityType:KClass<*> ):TId {
    return when(entityType) {
        Int::class  -> id.toInt() as TId
        Long::class -> id.toLong() as TId
        UUID::class -> id.toUUId() as TId
        else        -> id as TId
    }
}