package slatekit.data.encoders


import slatekit.common.crypto.Encryptor
import slatekit.query.QueryEncoder
import slatekit.common.Record
import slatekit.common.ext.orElse
import slatekit.data.Consts

class StringEncoder : SqlEncoder<String> {

    override fun encode(value: String?): String {
        return value?.let {
            val sValFinal = value.orElse("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): String? {
        return record.getString(name)
    }
}
