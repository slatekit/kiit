package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts

class FloatEncoder : SqlEncoder<Float> {

    override fun encode(value: Float?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Float? {
        return record.getFloat(name)
    }
}