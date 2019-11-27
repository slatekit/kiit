package slatekit.entities.core

import slatekit.common.ext.toUUId
import java.util.*
import kotlin.reflect.KClass
import slatekit.common.naming.Namer

@Suppress("UNCHECKED_CAST")
fun <TId> convertToId(id: String, entityType: KClass<*>): TId {
    return when (entityType) {
        Int::class -> id.toInt() as TId
        Long::class -> id.toLong() as TId
        UUID::class -> id.toUUId() as TId
        else -> id as TId
    }
}

fun buildTableName(entityType: KClass<*>, tableName: String?, namer: Namer?): String {
    val raw = if (tableName.isNullOrEmpty()) entityType.simpleName!! else tableName
    val table = namer?.rename(raw) ?: raw.toLowerCase()
    return table
}
