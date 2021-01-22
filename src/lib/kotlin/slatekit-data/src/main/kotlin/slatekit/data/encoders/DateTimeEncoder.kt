package slatekit.data.encoders

import slatekit.common.DateTime

import slatekit.common.Record
import slatekit.data.Consts
//import java.time.format.DateTimeFormatter
import org.threeten.bp.format.*
import slatekit.common.ext.atUtc
import slatekit.common.ext.atUtcLocal

class DateTimeEncoder : SqlEncoder<DateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun encode(value: DateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: DateTime?, isUTC: Boolean = false): String {
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
            record.getZonedDateTimeUtc(name).atUtcLocal()
        else
            record.getDateTime(name)
    }
}
