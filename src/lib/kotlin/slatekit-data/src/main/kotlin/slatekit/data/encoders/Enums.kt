package slatekit.data.encoders

import slatekit.common.*
import slatekit.common.Record
import slatekit.common.data.DataType
import slatekit.common.data.Value
import slatekit.meta.Reflector
import slatekit.data.Consts.NULL

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
            DataType.DTInt -> value?.let { it.value }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }

    fun decode(record: Record, name: String, dataCls: KClass<*>): EnumLike? {
        val enumInt = record.getInt(name)
        val enumValue = Reflector.getEnumValue(dataCls, enumInt)
        return enumValue
    }
}
