package slatekit.data.encoders

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Record
import slatekit.common.data.DataType
import slatekit.common.data.Value
import slatekit.common.ext.*
import slatekit.data.Consts

/**
 * Support for encoding to/from kotlin value to a SQL value
 * The encoders here are all for Date/Time based data types
 */
open class LocalDateEncoder(val dataType:DataType = DataType.DTLocalDate) : SqlEncoder<LocalDate> {
    override fun encode(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }

    override fun convert(value: LocalDate?): Value {
        val finalValue = when(dataType){
            DataType.DTInt -> value?.let { it.toNumeric() }
            else -> value
        }
        return Value("", dataType, finalValue, encode(value))
    }
}


open class LocalTimeEncoder(val dataType:DataType = DataType.DTLocalTime) : SqlEncoder<LocalTime> {
    override fun encode(value: LocalTime?): String {
        return value?.let { "'" + value.format(Consts.timeFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalTime? {
        return record.getLocalTimeOrNull(name)
    }

    override fun convert(value: LocalTime?): Value {
        val finalValue = when(dataType){
            DataType.DTInt -> value?.let { it.toNumeric() }
            else -> value
        }
        return Value("", dataType, finalValue, encode(value))
    }
}


open class LocalDateTimeEncoder(val dataType:DataType = DataType.DTLocalDateTime) : SqlEncoder<LocalDateTime> {

    override fun encode(value: LocalDateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: LocalDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) DateTimes.of(value).atUtc().local() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDateTime? {
        return toItem(record, name, false)
    }

    override fun convert(value: LocalDateTime?): Value {
        val finalValue = when(dataType){
            DataType.DTDouble -> value?.let { it.zoned().toEpochSecond() }
            else -> value
        }
        return Value("", dataType, finalValue, encode(value))
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): LocalDateTime? {
        return record.getLocalDateTimeOrNull(name)
    }
}


open class ZonedDateTimeEncoder(val dataType:DataType = DataType.DTZonedDateTime) : SqlEncoder<ZonedDateTime> {
    override fun encode(value: ZonedDateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: ZonedDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) it.atUtc() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): ZonedDateTime? {
        return toItem(record, name, false)
    }

    override fun convert(value: ZonedDateTime?): Value {
        val finalValue = when(dataType){
            DataType.DTDouble -> value?.let { it.toEpochSecond() }
            else -> value
        }
        return Value("", dataType, finalValue, encode(value))
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtcOrNull(name)
        else
            record.getZonedDateTimeOrNull(name)
    }
}


open class InstantEncoder(val dataType:DataType = DataType.DTInstant) : SqlEncoder<Instant> {
    override fun encode(value: Instant?): String {
        return value?.let {
            "'" + LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun convert(value: Instant?): Value {
        val finalValue = when(dataType){
            DataType.DTDouble -> value?.let { it.toEpochMilli() }
            else -> value
        }
        return Value("", dataType, finalValue, encode(value))
    }

    override fun decode(record: Record, name: String): Instant? {
        return record.getInstantOrNull(name)
    }
}


open class DateTimeEncoder(val dataType:DataType = DataType.DTDateTime) : SqlEncoder<DateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun encode(value: DateTime?): String {
        return toSql(value, false)
    }

    open fun toSql(value: DateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) value.atUtc() else value
            "'" + converted.format(dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): DateTime? {
        return toItem(record, name, false)
    }

    override fun convert(value: DateTime?): Value {
        val finalValue = when(dataType){
            DataType.DTDouble -> value?.let { it.toEpochSecond() }
            else -> value
        }
        return Value("", dataType, finalValue, encode(value))
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): DateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtcOrNull(name)?.atUtcLocal()
        else
            record.getDateTimeOrNull(name)
    }
}


