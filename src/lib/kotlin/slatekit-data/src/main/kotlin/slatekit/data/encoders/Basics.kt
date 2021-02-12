package slatekit.data.encoders


import slatekit.common.Record
import slatekit.common.data.DataType
import slatekit.common.data.Encoding
import slatekit.common.data.Value
import slatekit.common.ext.orElse
import slatekit.common.ext.zoned
import slatekit.data.Consts

/**
 * Support for encoding to/from kotlin value to a SQL value
 * The encoders here are all for basic data types ( bool, string, short, int, long, double )
 */

/**
 * @param dataType: This allows for supporting the conversion of bools to integers ( for sqlite )
 */
open class BoolEncoder(val dataType: DataType = DataType.DTBool) : SqlEncoder<Boolean> {
    override fun encode(value: Boolean?): String = value?.let { if (value) "1" else "0" } ?: Consts.NULL
    override fun decode(record: Record, name: String): Boolean? = record.getBoolOrNull(name)
    override fun convert(name:String, value: Boolean?): Value {
        val finalValue = when(dataType){
            DataType.DTInt -> value?.let { if(it) 1 else 0 }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class StringEncoder : SqlEncoder<String> {
    override fun encode(value: String?): String = value?.let { "'" + Encoding.ensureValue(value.orElse("")) + "'" } ?: Consts.NULL
    override fun decode(record: Record, name: String): String? = record.getStringOrNull(name)
    override fun convert(name:String, value: String?): Value = Value(name, DataType.DTString, value, encode(value))
}


/**
 * @param dataType: This allows for supporting the conversion of shorts to integers ( for sqlite )
 */
open class ShortEncoder(val dataType: DataType = DataType.DTShort) : SqlEncoder<Short> {
    override fun encode(value: Short?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Short? = record.getShortOrNull(name)
    override fun convert(name:String, value: Short?): Value {
        val finalValue = when(dataType){
            DataType.DTLong -> value?.let { it.toInt() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class IntEncoder(val dataType: DataType = DataType.DTInt) : SqlEncoder<Int> {
    override fun encode(value: Int?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Int? = record.getIntOrNull(name)
    override fun convert(name:String, value: Int?): Value {
        val finalValue = when(dataType){
            DataType.DTLong -> value?.let { it.toLong() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


/**
 * @param dataType: This allows for supporting the conversion of bools to integers ( for sqlite )
 */
open class LongEncoder(val dataType: DataType = DataType.DTLong) : SqlEncoder<Long> {
    override fun encode(value: Long?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Long? = record.getLongOrNull(name)
    override fun convert(name:String, value: Long?): Value = Value(name, dataType, value, encode(value))
}


/**
 * @param dataType: This allows for supporting the conversion of bools to integers ( for sqlite )
 */
open class FloatEncoder(val dataType: DataType = DataType.DTFloat) : SqlEncoder<Float> {
    override fun encode(value: Float?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Float? = record.getFloatOrNull(name)
    override fun convert(name:String, value: Float?): Value {
        val finalValue = when(dataType){
            DataType.DTDouble -> value?.let { it.toDouble() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class DoubleEncoder : SqlEncoder<Double> {
    override fun encode(value: Double?): String = value?.toString() ?: Consts.NULL
    override fun decode(record: Record, name: String): Double? = record.getDoubleOrNull(name)
    override fun convert(name:String, value: Double?): Value = Value(name, DataType.DTDouble, value, encode(value))
}
