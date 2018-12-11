package slatekit.common

import java.util.UUID

data class UniqueId(
    val prefix: String?,
    val uuid: UUID
) {
    val hasPrefix = !prefix.isNullOrEmpty()

    override fun toString(): String {
        return if (hasPrefix) prefix + ":" + uuid.toString() else uuid.toString()
    }

    companion object {

        @JvmStatic fun newId(): UniqueId {
            val uid = UUID.randomUUID()
            return UniqueId(null, uid)
        }

        @JvmStatic fun newId(prefix: String): UniqueId {
            val uid = UUID.randomUUID()
            return UniqueId(prefix, uid)
        }

        @JvmStatic fun fromString(uniqueId: String): UniqueId {
            val cleanid = uniqueId.trim { it <= ' ' }
            val ndxColon = cleanid.indexOf(":")
            var prefix = ""
            var uidText = cleanid
            val hasPrefix = ndxColon > -1
            if (hasPrefix) {
                prefix = cleanid.substring(0, ndxColon)
                uidText = cleanid.substring(ndxColon + 1)
            }
            val uid = UUID.fromString(uidText)
            return UniqueId(prefix, uid)
        }
    }
}
