package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.ids.UniqueId
import slatekit.common.records.Record
import slatekit.entities.Consts

object UniqueIdConverter : SqlConverter<UniqueId> {

    override fun toSql(value: UniqueId?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): UniqueId? {
        return record.getUniqueId(name)
    }
}