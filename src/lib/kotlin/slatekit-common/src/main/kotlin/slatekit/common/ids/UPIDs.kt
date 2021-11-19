package slatekit.common.ids

import java.util.UUID


/**
 * Universal PREFIX based on Unique Id, based on UUID V4
 * e.g. "usr:40d9b453-a9ed-4271-839b-061f0064ed77"
 */
data class UPID(val prefix: String, val uuid: UUID) : UID {
    override val name: String = "P"
    override val value: String = "$prefix:$uuid"
    override fun toString(): String = value
}


/**
 * Default implementation of the Ids interface with integration UPID ( using UUID V4 )
 */
object UPIDs : UIDGen<UPID> {
    val separator:String = ":"

    override fun create(): UPID {
        return create(null)
    }

    override fun create(context:String?): UPID {
        val uid = UUID.randomUUID()
        return UPID(context ?: "", uid)
    }

    override fun parse(id: String): UPID {
        val parts = split(id)
        return when(parts.size){
            1 -> UPID("", UUID.fromString(id))
            else -> UPID(parts[0], UUID.fromString(parts[1]))
        }
    }

    override fun split(id: String): Array<String> {
        val ndxSep = id.indexOf(separator)
        return when {
            ndxSep < 0 -> arrayOf(id)
            else -> arrayOf(id.substring(0, ndxSep), id.substring(ndxSep+1))
        }
    }
}
