package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts

class DoubleEncoder : SqlEncoder<Double> {

    override fun encode(value: Double?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Double? {
        return record.getDouble(name)
    }
}