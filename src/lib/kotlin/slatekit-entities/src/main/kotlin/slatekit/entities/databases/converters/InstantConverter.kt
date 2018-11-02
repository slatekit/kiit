package slatekit.entities.databases.converters

import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object InstantConverter : SqlConverter<Instant> {

    override fun toSql(value: Instant?): String {
        return value?.let {
            "'" + LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Instant? {
        return record.getInstant(name)
    }
}