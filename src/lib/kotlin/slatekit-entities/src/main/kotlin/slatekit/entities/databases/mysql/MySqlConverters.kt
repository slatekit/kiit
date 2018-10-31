package slatekit.entities.databases.mysql

import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.query.QueryEncoder
import slatekit.common.records.Record
import slatekit.meta.Reflector
import slatekit.entities.Consts.NULL
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass

object StringConverter : SqlConverter<String> {

    override fun toSql(value:String?): Any? {
        return value?.let {
            val sValFinal = value.nonEmptyOrDefault("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: NULL
    }

    fun toSql(value:String?, encrypt:Boolean, encryptor:Encryptor?): Any? {
        return value?.let {
            // Only encrypt on create
            val sValEnc = if (encrypt) encryptor?.encrypt(value) ?: value else value
            val sValFinal = sValEnc.nonEmptyOrDefault("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: NULL
    }

    override fun toItem(record: Record, name: String): String? {
        return record.getString(name)
    }
}

object BoolConverter : SqlConverter<Boolean> {

    override fun toSql(value:Boolean?): Any? {
        return value?.let { if (value) "1" else "0" } ?: NULL
    }

    override fun toItem(record: Record, name: String): Boolean? {
        return record.getBool(name)
    }
}

object ShortConverter : SqlConverter<Short> {

    override fun toSql(value:Short?): Any? {
        return value?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Short? {
        return record.getShort(name)
    }
}

object IntConverter : SqlConverter<Int> {

    override fun toSql(value: Int?): Any? {
        return value?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}

object LongConverter : SqlConverter<Long> {

    override fun toSql(value:Long?): Any? {
        return value?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}

object FloatConverter : SqlConverter<Float> {

    override fun toSql(value:Float?): Any? {
        return value?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Float? {
        return record.getFloat(name)
    }
}

object DoubleConverter : SqlConverter<Double> {

    override fun toSql(value:Double?): Any? {
        return value?.toString() ?: NULL
    }

    override fun toItem(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}

object LocalDateConverter : SqlConverter<LocalDate> {

    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun toSql(value:LocalDate?): Any? {
        return value?.let { "'" + value.format(dateFormat) + "'" } ?: NULL
    }

    override fun toItem(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}

object LocalTimeConverter : SqlConverter<LocalTime> {

    private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    override fun toSql(value:LocalTime?): Any? {
        return value?.let { "'" + value.format(timeFormat) + "'" } ?: NULL
    }

    override fun toItem(record: Record, name: String): LocalTime? {
        return record.getLocalTime(name)
    }
}

object InstantConverter : SqlConverter<Instant> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun toSql(value:Instant?): Any? {
        return value?.let {
            "'" + LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(dateTimeFormat) + "'"
        } ?: NULL
    }

    override fun toItem(record: Record, name: String): Instant? {
        return record.getInstant(name)
    }
}

class LocalDateTimeConverter(val isUTC:Boolean) : SqlConverter<LocalDateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun toSql(value:LocalDateTime?): Any? {
        return value?.let {
            val converted = if (isUTC) DateTime.of(value).atUtc().local() else value
            "'" + converted.format(dateTimeFormat) + "'"
        } ?: NULL
    }

    override fun toItem(record: Record, name: String): LocalDateTime? {
        return if (isUTC)
            record.getLocalDateTimeFromUTC(name)
        else
            record.getLocalDateTime(name)
    }
}

class ZonedDateTimeConverter(val isUTC:Boolean) : SqlConverter<ZonedDateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun toSql(value:ZonedDateTime?): Any? {
        return value?.let {
            val converted = if (isUTC) DateTime.of(value).atUtc().raw else value
            "'" + converted.format(dateTimeFormat) + "'"
        } ?: NULL
    }

    override fun toItem(record: Record, name: String): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeLocalFromUTC(name)
        else
            record.getZonedDateTime(name)
    }
}

class DateTimeConverter(val isUTC:Boolean) : SqlConverter<DateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun toSql(value:DateTime?): Any? {
        return value?.let { "'" + value.format(dateTimeFormat) + "'" } ?: NULL
    }

    override fun toItem(record: Record, name: String): DateTime? {
        return if (isUTC)
            record.getDateTimeLocalFromUTC(name)
        else
            record.getDateTime(name)
    }
}

object UUIDConverter : SqlConverter<UUID> {

    override fun toSql(value:UUID?): Any? {
        return value?.let { "'" + value.toString() + "'" } ?: NULL
    }

    override fun toItem(record: Record, name: String): UUID? {
        return record.getUUID(name)
    }
}

object UniqueIdConverter : SqlConverter<UniqueId> {

    override fun toSql(value:UniqueId?): Any? {
        return value?.let { "'" + value.toString() + "'" } ?: NULL
    }

    override fun toItem(record: Record, name: String): UniqueId? {
        return record.getUniqueId(name)
    }
}

class EnumConverter(val dataCls: KClass<*>) : SqlConverter<EnumLike> {

    override fun toSql(value:EnumLike?): Any? {
        return value?.let { "'" + value.toString() + "'" } ?: NULL
    }

    override fun toItem(record: Record, name: String): EnumLike? {
        val enumInt = record.getInt(name)
        val enumValue = Reflector.getEnumValue(dataCls, enumInt)
        return enumValue
    }
}