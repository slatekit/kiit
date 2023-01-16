package kiit.common.ids

import java.util.*



/**
 * Wrapper for UUID V4 conforming to allow for using
 */
data class UUID4(val uuid: UUID) : UID {
    override val name: String = "4"
    override val value: String = uuid.toString()
    override fun toString(): String = value
}


/**
 * Default implementation of the Ids interface with integration with a UUID v4
 */
object UUIDs : UIDGen<UUID4> {

    override fun create(): UUID4 {
        return UUID4(UUID.randomUUID())
    }

    override fun create(context:String?): UUID4 {
        return UUID4(UUID.randomUUID())
    }

    override fun parse(id: String):UUID4 {
        return UUID4(UUID.fromString(id))
    }

    override fun split(id: String): Array<String> {
        return try {
            val uuid = UUID.fromString(id)
            arrayOf(
                    uuid.timestamp().toString(),
                    uuid.version().toString(),
                    uuid.variant().toString(),
                    uuid.clockSequence().toString(),
                    uuid.node().toString())
        } catch(ex:Exception) {
            arrayOf()
        }
    }
}