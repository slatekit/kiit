package slatekit.orm.databases.converters

import slatekit.orm.databases.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts
//import java.time.*
import org.threeten.bp.*

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