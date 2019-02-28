package slatekit.orm.databases.converters

import slatekit.orm.databases.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts

object BoolConverter : SqlConverter<Boolean> {

    override fun toSql(value: Boolean?): String {
        return value?.let { if (value) "1" else "0" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Boolean? {
        return record.getBool(name)
    }
}