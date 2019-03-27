package slatekit.orm.databases.converters

import slatekit.common.*
import slatekit.common.Record
import slatekit.meta.Reflector
import slatekit.orm.Consts.NULL
import slatekit.orm.core.SqlConverter
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