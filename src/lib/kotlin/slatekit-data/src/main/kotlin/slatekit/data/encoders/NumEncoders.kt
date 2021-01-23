package slatekit.data.encoders

import slatekit.common.Record
import slatekit.data.Consts

open class ShortEncoder : SqlEncoder<Short> {

    override fun encode(value: Short?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Short? {
        return record.getShortOrNull(name)
    }
}


open class IntEncoder : SqlEncoder<Int> {

    override fun encode(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Int? {
        return record.getIntOrNull(name)
    }
}


open class LongEncoder : SqlEncoder<Long> {

    override fun encode(value: Long?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Long? {
        return record.getLongOrNull(name)
    }
}


open class FloatEncoder : SqlEncoder<Float> {

    override fun encode(value: Float?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Float? {
        return record.getFloatOrNull(name)
    }
}


open class DoubleEncoder : SqlEncoder<Double> {

    override fun encode(value: Double?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Double? {
        return record.getDoubleOrNull(name)
    }
}
