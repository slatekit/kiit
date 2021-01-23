package slatekit.data.encoders

import slatekit.common.Record
import slatekit.data.Consts
import slatekit.data.encoders.SqlEncoder

open class ShortEncoder : SqlEncoder<Short> {

    override fun encode(value: Short?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Short? {
        return record.getShort(name)
    }
}


open class IntEncoder : SqlEncoder<Int> {

    override fun encode(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}


open class LongEncoder : SqlEncoder<Long> {

    override fun encode(value: Long?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}


open class FloatEncoder : SqlEncoder<Float> {

    override fun encode(value: Float?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Float? {
        return record.getFloat(name)
    }
}


open class DoubleEncoder : SqlEncoder<Double> {

    override fun encode(value: Double?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}
