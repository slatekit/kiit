package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.encrypt.Encryptor
import slatekit.query.QueryEncoder
import slatekit.common.Record
import slatekit.common.ext.orElse
import slatekit.orm.Consts

object StringEncoder : SqlEncoder<String> {

    override fun encode(value: String?): String {
        return value?.let {
            val sValFinal = value.orElse("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: Consts.NULL
    }

    fun toSql(value: String?, encrypt: Boolean, encryptor: Encryptor?): String {
        return value?.let {
            // Only encrypt on create
            val sValEnc = if (encrypt) encryptor?.encrypt(value) ?: value else value
            val sValFinal = sValEnc.orElse("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): String? {
        return record.getString(name)
    }
}