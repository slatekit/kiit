package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts

object ShortEncoder : SqlEncoder<Short> {

    override fun encode(value: Short?): String {
        return value?.toString() ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Short? {
        return record.getShort(name)
    }
}