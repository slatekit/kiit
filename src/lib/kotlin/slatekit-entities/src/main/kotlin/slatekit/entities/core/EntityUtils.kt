package slatekit.entities.core

import java.util.*

object EntityUtils {

    fun <TId> isCreated(id: TId): Boolean {
        return when (id) {
            is Int -> id > 0
            is Long -> id > 0L
            is String -> !id.isEmpty()
            is UUID -> !id.toString().trim().isEmpty()
            else -> false
        }
    }
}
