package slatekit.orm.databases.converters

import slatekit.orm.core.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts
import java.util.*

object UUIDConverter : SqlConverter<UUID> {

    override fun toSql(value: UUID?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): UUID? {
        return record.getUUID(name)
    }
}