package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts

class ShortEncoder : SqlEncoder<Short> {

    override fun encode(value: Short?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Short? {
        return record.getShort(name)
    }
}
