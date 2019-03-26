package slatekit.orm.databases.converters

import slatekit.orm.core.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts
//import java.time.*
import org.threeten.bp.*
import slatekit.common.ext.atUtc

object ZonedDateTimeConverter : SqlConverter<ZonedDateTime> {

    override fun toSql(value: ZonedDateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: ZonedDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) it.atUtc() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): ZonedDateTime? {
        return toItem(record, name, false)
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtc(name)
        else
            record.getZonedDateTime(name)
    }
}