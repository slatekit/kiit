package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts

open class LongEncoder : SqlEncoder<Long> {

    override fun encode(value: Long?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}
