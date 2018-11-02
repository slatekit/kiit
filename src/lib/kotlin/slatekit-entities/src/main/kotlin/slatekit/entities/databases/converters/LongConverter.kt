package slatekit.entities.databases.converters

import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts

object LongConverter : SqlConverter<Long> {

    override fun toSql(value: Long?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}