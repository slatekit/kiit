package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts
//import java.time.*
import org.threeten.bp.*
import slatekit.common.DateTimes
import slatekit.common.ext.atUtc
import slatekit.common.ext.local

open class LocalDateTimeEncoder : SqlEncoder<LocalDateTime> {

    override fun encode(value: LocalDateTime?): String {
        return toSql(value, false)
    }


    fun toSql(value: LocalDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) DateTimes.of(value).atUtc().local() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDateTime? {
        return toItem(record, name, false)
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): LocalDateTime? {
        return record.getLocalDateTime(name)
    }
}
