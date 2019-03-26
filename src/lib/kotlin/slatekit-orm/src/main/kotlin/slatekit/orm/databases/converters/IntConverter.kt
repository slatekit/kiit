package slatekit.orm.databases.converters

import slatekit.orm.core.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts

object IntConverter : SqlConverter<Int> {

    override fun toSql(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}