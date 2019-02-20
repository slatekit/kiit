package slatekit.entities.databases.converters

import slatekit.entities.databases.SqlConverter
import slatekit.common.encrypt.Encryptor
import slatekit.common.nonEmptyOrDefault
import slatekit.common.query.QueryEncoder
import slatekit.common.Record
import slatekit.entities.Consts

object StringConverter : SqlConverter<String> {

    override fun toSql(value: String?): String {
        return value?.let {
            val sValFinal = value.nonEmptyOrDefault("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: Consts.NULL
    }

    fun toSql(value: String?, encrypt: Boolean, encryptor: Encryptor?): String {
        return value?.let {
            // Only encrypt on create
            val sValEnc = if (encrypt) encryptor?.encrypt(value) ?: value else value
            val sValFinal = sValEnc.nonEmptyOrDefault("")
            "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        } ?: Consts.NULL
    }

    override fun toItem(record: Record, name: String): String? {
        return record.getString(name)
    }
}