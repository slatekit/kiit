package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.Record
import slatekit.entities.Consts
import java.util.*

object UUIDConverter : SqlConverter<UUID> {

    override fun toSql(value: UUID?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): UUID? {
        return record.getUUID(name)
    }
}