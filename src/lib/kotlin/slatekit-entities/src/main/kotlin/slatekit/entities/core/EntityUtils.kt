package slatekit.entities.core

import slatekit.common.naming.Namer
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


fun buildTableName(entityType: KClass<*>, tableName:String?, namer: Namer?):String {
    val rawTableName = tableName ?: entityType.simpleName!! // "user"
    val table = namer?.rename(rawTableName) ?: rawTableName.toLowerCase()
    return table
}