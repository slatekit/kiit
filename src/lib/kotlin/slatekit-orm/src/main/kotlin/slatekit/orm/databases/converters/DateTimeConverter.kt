package slatekit.orm.databases.converters

import slatekit.common.DateTime
import slatekit.orm.core.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts
//import java.time.format.DateTimeFormatter
import org.threeten.bp.format.*
import slatekit.common.ext.atUtc
import slatekit.common.ext.atUtcLocal

object DateTimeConverter : SqlConverter<DateTime> {
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun toSql(value: DateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: DateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) value.atUtc() else value
            "'" + converted.format(dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): DateTime? {
        return toItem(record, name, false)
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): DateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtc(name).atUtcLocal()
        else
            record.getDateTime(name)
    }
}