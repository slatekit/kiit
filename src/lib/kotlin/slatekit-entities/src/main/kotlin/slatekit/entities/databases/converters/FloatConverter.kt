package slatekit.entities.databases.converters

import slatekit.common.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts

object FloatConverter : SqlConverter<Float> {

    override fun toSql(value: Float?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Float? {
        return record.getFloat(name)
    }
}