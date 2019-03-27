package slatekit.orm.databases.converters

import slatekit.orm.core.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts
//import java.time.*
import org.threeten.bp.*

object LocalTimeConverter : SqlConverter<LocalTime> {

    override fun toSql(value: LocalTime?): String {
        return value?.let { "'" + value.format(Consts.timeFormat) + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): LocalTime? {
        return record.getLocalTime(name)
    }
}