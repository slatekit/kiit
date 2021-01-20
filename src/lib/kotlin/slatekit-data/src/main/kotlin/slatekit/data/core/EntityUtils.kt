package slatekit.data.slatekit.data.core

import slatekit.common.ext.toUUId
import java.util.*
import kotlin.reflect.KClass

object EntityUtils {
    @Suppress("UNCHECKED_CAST")
    fun <TId> convertToId(id: String, entityType: KClass<*>): TId {
        return when (entityType) {
            Int::class -> id.toInt() as TId
            Long::class -> id.toLong() as TId
            UUID::class -> id.toUUId() as TId
            else -> id as TId
        }
    }
}
