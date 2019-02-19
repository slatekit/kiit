package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.Record
import slatekit.entities.Consts

object ShortConverter : SqlConverter<Short> {

    override fun toSql(value: Short?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Short? {
        return record.getShort(name)
    }
}