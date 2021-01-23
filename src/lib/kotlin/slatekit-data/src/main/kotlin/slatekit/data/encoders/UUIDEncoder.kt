package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts
import java.util.*

open class UUIDEncoder : SqlEncoder<UUID> {

    override fun encode(value: UUID?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UUID? {
        return record.getUUID(name)
    }
}
