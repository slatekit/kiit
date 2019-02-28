package slatekit.orm.databases.converters

import slatekit.orm.databases.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts

object FloatConverter : SqlConverter<Float> {

    override fun toSql(value: Float?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Float? {
        return record.getFloat(name)
    }
}