package kiit.data.encoders

import slatekit.common.*
import slatekit.common.values.Record
import slatekit.common.data.DataType
import slatekit.common.data.Value
import kiit.meta.Reflector
import kiit.data.Consts.NULL

import kotlin.reflect.KClass


open class EnumEncoder(val dataType:DataType = DataType.DTEnum) : SqlEncoder<EnumLike> {

    override fun encode(value: EnumLike?): String {
        return value?.let { value.value.toString() } ?: NULL
    }

    override fun decode(record: Record, name: String): EnumLike? {
        return null
    }

    override fun convert(name:String, value: EnumLike?): Value {
        val finalValue = when(dataType){
            DataType.DTInt  -> value?.let { it.value }
            DataType.DTLong -> value?.let { it.value.toLong() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }

    fun decode(record: Record, name: String, dataCls: KClass<*>): EnumLike? {
        val enumInt = when(dataType) {
            DataType.DTInt -> record.getInt(name)
            DataType.DTLong -> record.getLong(name).toInt()
            else -> record.getInt(name)
        }
        val enumValue = Reflector.getEnumValue(dataCls, enumInt)
        return enumValue
    }
}
