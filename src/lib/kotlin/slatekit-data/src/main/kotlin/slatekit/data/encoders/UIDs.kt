package slatekit.data.encoders

import slatekit.common.Record
import slatekit.common.data.DataType
import slatekit.common.data.Value
import slatekit.common.ids.UPID
import slatekit.data.Consts
import java.util.*

open class UUIDEncoder : SqlEncoder<UUID> {
    override fun encode(value: UUID?): String = value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    override fun decode(record: Record, name: String): UUID? = record.getUUIDOrNull(name)
    override fun convert(name:String, value: UUID?): Value {
        return Value(name, DataType.DTString, value?.toString(), encode(value))
    }
}


open class UPIDEncoder : SqlEncoder<UPID> {
    override fun encode(value: UPID?): String = value?.let { "'${value.value}'" } ?: Consts.NULL
    override fun decode(record: Record, name: String): UPID? = record.getUPIDOrNull(name)
    override fun convert(name:String, value: UPID?): Value {
        return Value(name, DataType.DTString, value?.value, encode(value))
    }
}
