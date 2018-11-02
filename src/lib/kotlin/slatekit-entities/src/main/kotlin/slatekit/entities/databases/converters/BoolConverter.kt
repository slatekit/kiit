package slatekit.entities.databases.converters

import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts

object BoolConverter : SqlConverter<Boolean> {

    override fun toSql(value: Boolean?): String {
        return value?.let { if (value) "1" else "0" } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Boolean? {
        return record.getBool(name)
    }
}