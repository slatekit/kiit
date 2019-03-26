package slatekit.orm.databases.converters

import slatekit.orm.core.SqlConverter
import slatekit.common.encrypt.Encryptor
import slatekit.common.nonEmptyOrDefault
import slatekit.query.QueryEncoder
import slatekit.common.Record
import slatekit.orm.Consts

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