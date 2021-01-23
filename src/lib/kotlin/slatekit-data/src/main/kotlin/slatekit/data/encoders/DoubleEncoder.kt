package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts

open class DoubleEncoder : SqlEncoder<Double> {

    override fun encode(value: Double?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}
