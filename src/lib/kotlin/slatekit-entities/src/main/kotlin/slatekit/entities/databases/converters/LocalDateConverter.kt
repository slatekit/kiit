package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.Record
import slatekit.entities.Consts
//import java.time.*
import org.threeten.bp.*

object LocalDateConverter : SqlConverter<LocalDate> {

    override fun toSql(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}