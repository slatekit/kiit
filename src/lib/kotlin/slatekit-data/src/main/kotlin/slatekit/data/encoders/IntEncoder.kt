package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts

open class IntEncoder : SqlEncoder<Int> {

    override fun encode(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}
