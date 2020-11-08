package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.ids.UPID
import slatekit.common.Record
import slatekit.orm.Consts

class UPIDEncoder : SqlEncoder<UPID> {

    override fun encode(value: UPID?): String {
        return value?.let { "'${value.value}'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UPID? {
        return record.getUPID(name)
    }
}