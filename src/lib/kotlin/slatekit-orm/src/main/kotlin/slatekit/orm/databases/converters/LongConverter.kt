package slatekit.orm.databases.converters

import slatekit.orm.core.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts

object LongConverter : SqlConverter<Long> {

    override fun toSql(value: Long?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}