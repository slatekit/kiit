package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.ids.UniqueId
import slatekit.common.Record
import slatekit.orm.Consts

class UniqueIdEncoder : SqlEncoder<UniqueId> {

    override fun encode(value: UniqueId?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UniqueId? {
        return record.getUniqueId(name)
    }
}