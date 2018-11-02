package slatekit.entities.databases.converters

import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts
import java.time.LocalDate

object LocalDateConverter : SqlConverter<LocalDate> {

    override fun toSql(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}