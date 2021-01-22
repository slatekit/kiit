package slatekit.data.encoders


import slatekit.common.ids.UPID
import slatekit.common.Record
import slatekit.data.Consts

class UPIDEncoder : SqlEncoder<UPID> {

    override fun encode(value: UPID?): String {
        return value?.let { "'${value.value}'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): UPID? {
        return record.getUPID(name)
    }
}
