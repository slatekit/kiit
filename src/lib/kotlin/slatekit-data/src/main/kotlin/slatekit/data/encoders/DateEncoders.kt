package slatekit.data.encoders

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Record
import slatekit.common.ext.atUtc
import slatekit.common.ext.atUtcLocal
import slatekit.common.ext.local
import slatekit.data.Consts


open class LocalDateEncoder : SqlEncoder<LocalDate> {

    override fun encode(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}


open class LocalTimeEncoder : SqlEncoder<LocalTime> {

    override fun encode(value: LocalTime?): String {
        return value?.let { "'" + value.format(Consts.timeFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalTime? {
        return record.getLocalTimeOrNull(name)
    }
}


open class LocalDateTimeEncoder : SqlEncoder<LocalDateTime> {

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

    fun toItem(record: Record, name: String, isUTC: Boolean = false): LocalDateTime? {
        return record.getLocalDateTimeOrNull(name)
    }
}


open class ZonedDateTimeEncoder : SqlEncoder<ZonedDateTime> {

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

    fun toItem(record: Record, name: String, isUTC: Boolean = false): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtcOrNull(name)
        else
            record.getZonedDateTimeOrNull(name)
    }
}


open class InstantEncoder : SqlEncoder<Instant> {

    override fun encode(value: Instant?): String {
        return value?.let {
            "'" + LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Instant? {
        return record.getInstantOrNull(name)
    }
}


open class DateTimeEncoder : SqlEncoder<DateTime> {
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

    fun toItem(record: Record, name: String, isUTC: Boolean = false): DateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtcOrNull(name)?.atUtcLocal()
        else
            record.getDateTimeOrNull(name)
    }
}


