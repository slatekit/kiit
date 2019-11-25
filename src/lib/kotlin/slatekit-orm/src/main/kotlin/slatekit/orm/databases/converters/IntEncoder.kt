package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts

object IntEncoder : SqlEncoder<Int> {

    override fun encode(value: Int?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Int? {
        return record.getInt(name)
    }
}