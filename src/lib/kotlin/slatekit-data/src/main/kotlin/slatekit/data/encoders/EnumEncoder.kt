package slatekit.data.encoders

import slatekit.common.*
import slatekit.common.Record
import slatekit.meta.Reflector
import slatekit.data.Consts.NULL

import kotlin.reflect.KClass


open class EnumEncoder : SqlEncoder<EnumLike> {

    override fun encode(value: EnumLike?): String {
        return value?.let { value.value.toString() } ?: NULL
    }

    override fun decode(record: Record, name: String): EnumLike? {
        return null
    }

    fun toItem(record: Record, name: String, dataCls: KClass<*>): EnumLike? {
        val enumInt = record.getInt(name)
        val enumValue = Reflector.getEnumValue(dataCls, enumInt)
        return enumValue
    }
}
