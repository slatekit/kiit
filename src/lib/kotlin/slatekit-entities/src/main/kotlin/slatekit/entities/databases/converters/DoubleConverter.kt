package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.records.Record
import slatekit.entities.Consts

object DoubleConverter : SqlConverter<Double> {

    override fun toSql(value: Double?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}