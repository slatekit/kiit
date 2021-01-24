package slatekit.data.encoders

import slatekit.common.Record
import slatekit.common.data.Encoding
import slatekit.common.ext.orElse
import slatekit.data.Consts

open class StringEncoder : SqlEncoder<String> {

    override fun encode(value: String?): String {
        return value?.let {
            "'" + Encoding.ensureValue(value.orElse("")) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): String? {
        return record.getStringOrNull(name)
    }
}
