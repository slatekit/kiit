package slatekit.data.encoders


import slatekit.common.Record
import slatekit.common.ids.UPID
import slatekit.data.Consts
import java.util.*

open class UUIDEncoder : SqlEncoder<UUID> {

    override fun encode(value: UUID?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UUID? {
        return record.getUUIDOrNull(name)
    }
}


open class UPIDEncoder : SqlEncoder<UPID> {

    override fun encode(value: UPID?): String {
        return value?.let { "'${value.value}'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UPID? {
        return record.getUPIDOrNull(name)
    }
}
