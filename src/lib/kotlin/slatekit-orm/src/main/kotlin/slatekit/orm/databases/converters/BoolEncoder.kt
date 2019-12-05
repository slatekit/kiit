package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts

class BoolEncoder : SqlEncoder<Boolean> {

    override fun encode(value: Boolean?): String {
        return value?.let { if (value) "1" else "0" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Boolean? {
        return record.getBool(name)
    }
}