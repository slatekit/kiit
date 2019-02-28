package slatekit.orm.databases.converters

import slatekit.orm.databases.SqlConverter
import slatekit.common.Record
import slatekit.orm.Consts

object DoubleConverter : SqlConverter<Double> {

    override fun toSql(value: Double?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}