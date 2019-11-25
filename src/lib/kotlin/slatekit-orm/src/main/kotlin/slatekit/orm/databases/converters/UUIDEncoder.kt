package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts
import java.util.*

object UUIDEncoder : SqlEncoder<UUID> {

    override fun encode(value: UUID?): String {
        return value?.let { "'" + value.toString() + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UUID? {
        return record.getUUID(name)
    }
}