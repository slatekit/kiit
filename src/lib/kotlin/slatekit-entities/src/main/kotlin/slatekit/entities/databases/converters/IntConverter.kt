package slatekit.entities.databases.converters

import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts

object IntConverter : SqlConverter<Int> {

    override fun toSql(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}