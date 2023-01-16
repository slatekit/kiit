package kiit.data.encoders

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import kiit.common.DateTime
import kiit.common.DateTimes
import kiit.common.values.Record
import kiit.common.data.DataType
import kiit.common.data.Value
import kiit.common.ext.*
import kiit.data.Consts

/**
 * Support for encoding to/from kotlin value to a SQL value
 * The encoders here are all for Date/Time based data types
 */
open class LocalDateEncoder(val dataType: DataType = DataType.DTLocalDate) : SqlEncoder<LocalDate> {
    protected open val DBFormat = DateTimeFormatter.ofPattern("yyyyMMdd")

    override fun encode(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDate? {
        return when (dataType) {
            DataType.DTInt -> {
                val num = record.getIntOrNull(name)
                num?.let {
                    LocalDate.parse(it.toString(), DBFormat)
                }
            }
            else -> {
                record.getLocalDateOrNull(name)
            }
        }
    }

    override fun convert(name: String, value: LocalDate?): Value {
        val finalValue = when (dataType) {
            DataType.DTInt -> value?.let { it.toNumeric() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class LocalTimeEncoder(val dataType: DataType = DataType.DTLocalTime) : SqlEncoder<LocalTime> {
    protected open val DBFormat = DateTimeFormatter.ofPattern("HHmmss")
    override fun encode(value: LocalTime?): String {
        return value?.let { "'" + value.format(Consts.timeFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalTime? {
        return when (dataType) {
            DataType.DTInt -> {
                val num = record.getIntOrNull(name)
                num?.let {
                    LocalTime.parse(it.toString(), DBFormat)
                }
            }
            else -> {
                record.getLocalTimeOrNull(name)
            }
        }
    }

    override fun convert(name: String, value: LocalTime?): Value {
        val finalValue = when (dataType) {
            DataType.DTInt -> value?.let { it.toNumeric() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class LocalDateTimeEncoder(val dataType: DataType = DataType.DTLocalDateTime) : SqlEncoder<LocalDateTime> {
    protected open val offset = OffsetDateTime.now().offset

    override fun encode(value: LocalDateTime?): String {
        return toSql(value, false)
    }

    override fun decode(record: Record, name: String): LocalDateTime? {
        return record.getLocalDateTimeOrNull(name)
    }

    fun toSql(value: LocalDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) DateTimes.of(value).atUtc().local() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }


    override fun convert(name: String, value: LocalDateTime?): Value {
        val finalValue = when (dataType) {
            DataType.DTLong -> value?.let { it.toInstant(offset).toEpochMilli() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class DateTimeEncoder(val dataType: DataType = DataType.DTDateTime, private val utc: Boolean = true) : SqlEncoder<DateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun encode(value: DateTime?): String {
        return value?.let {
            val converted = if (utc) value.atUtc() else value
            "'" + converted.format(dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): DateTime? {
        return if (utc)
            record.getZonedDateTimeUtcOrNull(name)?.atUtcLocal()
        else
            record.getDateTimeOrNull(name)
    }

    override fun convert(name: String, value: DateTime?): Value {
        val finalValue = when (dataType) {
            DataType.DTLong -> value?.let { it.toInstant().toEpochMilli() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class ZonedDateTimeEncoder(val dataType: DataType = DataType.DTZonedDateTime, private val utc: Boolean = true) : SqlEncoder<ZonedDateTime> {
    protected open val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun encode(value: ZonedDateTime?): String {
        return value?.let {
            val converted = if (utc) it.atUtc() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): ZonedDateTime? {
        return if (utc)
            record.getZonedDateTimeUtcOrNull(name)
        else
            record.getZonedDateTimeOrNull(name)
    }

    override fun convert(name: String, value: ZonedDateTime?): Value {
        val finalValue = when (dataType) {
            DataType.DTLong -> value?.let { it.toInstant().toEpochMilli() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }
}


open class InstantEncoder(val dataType: DataType = DataType.DTInstant) : SqlEncoder<Instant> {
    override fun encode(value: Instant?): String {
        return value?.let {
            "'" + LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun convert(name: String, value: Instant?): Value {
        val finalValue = when (dataType) {
            DataType.DTLong -> value?.let { it.toEpochMilli() }
            else -> value
        }
        return Value(name, dataType, finalValue, encode(value))
    }

    override fun decode(record: Record, name: String): Instant? {
        return record.getInstantOrNull(name)
    }
}


