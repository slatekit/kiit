package slatekit.data.encoders


import slatekit.common.Record
import slatekit.common.data.Encoding
import slatekit.common.ext.orElse
import slatekit.data.Consts

open class BoolEncoder : SqlEncoder<Boolean> {
    override fun encode(value: Boolean?): String = value?.let { if (value) "1" else "0" } ?: Consts.NULL
    override fun decode(record: Record, name: String): Boolean? = record.getBoolOrNull(name)
}


open class StringEncoder : SqlEncoder<String> {
    override fun encode(value: String?): String = value?.let { "'" + Encoding.ensureValue(value.orElse("")) + "'" } ?: Consts.NULL
    override fun decode(record: Record, name: String): String? = record.getStringOrNull(name)
}


open class ShortEncoder : SqlEncoder<Short> {
    override fun encode(value: Short?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Short? = record.getShortOrNull(name)
}


open class IntEncoder : SqlEncoder<Int> {
    override fun encode(value: Int?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Int? = record.getIntOrNull(name)
}


open class LongEncoder : SqlEncoder<Long> {
    override fun encode(value: Long?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Long? = record.getLongOrNull(name)
}


open class FloatEncoder : SqlEncoder<Float> {
    override fun encode(value: Float?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Float? = record.getFloatOrNull(name)
}


open class DoubleEncoder : SqlEncoder<Double> {
    override fun encode(value: Double?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Double? = record.getDoubleOrNull(name)
}
