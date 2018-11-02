package slatekit.entities.databases.converters

import slatekit.common.*
import slatekit.common.records.Record
import slatekit.meta.Reflector
import slatekit.entities.Consts.NULL
import kotlin.reflect.KClass


object EnumConverter : SqlConverter<EnumLike> {

    override fun toSql(value: EnumLike?): String {
        return value?.let { value.value.toString() } ?: NULL
    }

    override fun toItem(record: Record, name: String): EnumLike? {
        return null
    }

    fun toItem(record: Record, name: String, dataCls: KClass<*>): EnumLike? {
        val enumInt = record.getInt(name)
        val enumValue = Reflector.getEnumValue(dataCls, enumInt)
        return enumValue
    }
}