package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts

object LongEncoder : SqlEncoder<Long> {

    override fun encode(value: Long?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Long? {
        return record.getLong(name)
    }
}