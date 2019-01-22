package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts
import java.time.LocalTime

object LocalTimeConverter : SqlConverter<LocalTime> {

    override fun toSql(value: LocalTime?): String {
        return value?.let { "'" + value.format(Consts.timeFormat) + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): LocalTime? {
        return record.getLocalTime(name)
    }
}