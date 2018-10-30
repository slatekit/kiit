package slatekit.entities.databases.postgres

import slatekit.common.DateTime
import slatekit.common.EnumLike
import slatekit.common.SqlConverter
import slatekit.common.UniqueId
import slatekit.common.records.Record
import slatekit.meta.Reflector
import slatekit.entities.Consts.NULL
import java.time.*
import java.util.*
import kotlin.reflect.KClass

object StringConverter : SqlConverter<String> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as String?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): String? {
        return record.getString(name)
    }
}

object BoolConverter : SqlConverter<Boolean> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Boolean?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Boolean? {
        return record.getBool(name)
    }
}

object ShortConverter : SqlConverter<Short> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Short?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Short? {
        return record.getShort(name)
    }
}

object IntConverter : SqlConverter<Int> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Int?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}

object LongConverter : SqlConverter<Long> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Long?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}

object FloatConverter : SqlConverter<Float> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Float?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Float? {
        return record.getFloat(name)
    }
}

object DoubleConverter : SqlConverter<Double> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Double?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}

object LocalDateConverter : SqlConverter<LocalDate> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as LocalDate?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}

object LocalTimeConverter : SqlConverter<LocalTime> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as LocalTime?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): LocalTime? {
        return record.getLocalTime(name)
    }
}

object InstantConverter : SqlConverter<Instant> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as Instant?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Instant? {
        return record.getInstant(name)
    }
}

class LocalDateTimeConverter(val isUTC:Boolean) : SqlConverter<LocalDateTime> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as LocalDateTime?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): LocalDateTime? {
        return if (isUTC)
            record.getLocalDateTimeFromUTC(name)
        else
            record.getLocalDateTime(name)
    }
}

class ZonedDateTimeConverter(val isUTC:Boolean) : SqlConverter<ZonedDateTime> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as ZonedDateTime?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeLocalFromUTC(name)
        else
            record.getZonedDateTime(name)
    }
}

class DateTimeConverter(val isUTC:Boolean) : SqlConverter<DateTime> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as DateTime?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): DateTime? {
        return if (isUTC)
            record.getDateTimeLocalFromUTC(name)
        else
            record.getDateTime(name)
    }
}

object UUIDConverter : SqlConverter<UUID> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as UUID?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): UUID? {
        return record.getUUID(name)
    }
}

object UniqueIdConverter : SqlConverter<UniqueId> {

    override fun toSql(item: Any, name: String): Any? {
        return (Reflector.getFieldValue(item, name) as UniqueId?)?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): UniqueId? {
        return record.getUniqueId(name)
    }
}

class EnumConverter(val dataCls: KClass<*>) : SqlConverter<EnumLike> {

    override fun toSql(item: Any, name: String): Any? {
        val raw = Reflector.getFieldValue(item, name) as EnumLike
        return "'" + raw.value.toString() + "'"
    }

    override fun toItem(record: Record, name: String): EnumLike? {
        val enumInt = record.getInt(name)
        val enumValue = Reflector.getEnumValue(dataCls, enumInt)
        return enumValue
    }
}