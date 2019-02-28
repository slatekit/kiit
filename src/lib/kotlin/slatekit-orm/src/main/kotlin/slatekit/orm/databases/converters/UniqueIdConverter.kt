package slatekit.orm.databases.converters

import slatekit.orm.databases.SqlConverter
import slatekit.common.ids.UniqueId
import slatekit.common.Record
import slatekit.orm.Consts

object UniqueIdConverter : SqlConverter<UniqueId> {

    override fun toSql(value: UniqueId?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): UniqueId? {
        return record.getUniqueId(name)
    }
}