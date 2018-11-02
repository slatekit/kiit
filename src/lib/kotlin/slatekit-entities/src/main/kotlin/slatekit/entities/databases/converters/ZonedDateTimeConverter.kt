package slatekit.entities.databases.converters

import slatekit.common.DateTime
import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts
import java.time.ZonedDateTime

object ZonedDateTimeConverter : SqlConverter<ZonedDateTime> {

    override fun toSql(value: ZonedDateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: ZonedDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) DateTime.of(value).atUtc().raw else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): ZonedDateTime? {
        return toItem(record, name, false)
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeLocalFromUTC(name)
        else
            record.getZonedDateTime(name)
    }
}