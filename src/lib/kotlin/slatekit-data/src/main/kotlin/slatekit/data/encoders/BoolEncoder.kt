package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts

open class BoolEncoder : SqlEncoder<Boolean> {

    override fun encode(value: Boolean?): String {
        return value?.let { if (value) "1" else "0" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Boolean? {
        return record.getBool(name)
    }
}
