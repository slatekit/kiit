package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.Record
import slatekit.entities.Consts

object IntConverter : SqlConverter<Int> {

    override fun toSql(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}