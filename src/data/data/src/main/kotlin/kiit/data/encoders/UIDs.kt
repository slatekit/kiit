package kiit.data.encoders

import kiit.common.values.Record
import kiit.common.data.DataType
import kiit.common.data.Value
import kiit.common.ids.UPID
import kiit.data.Consts
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
