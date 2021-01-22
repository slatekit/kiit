package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts
//import java.time.*
import org.threeten.bp.*
import slatekit.common.ext.atUtc

class ZonedDateTimeEncoder : SqlEncoder<ZonedDateTime> {

    override fun encode(value: ZonedDateTime?): String {
        return toSql(value, false)
    }

    fun toSql(value: ZonedDateTime?, isUTC: Boolean = false): String {
        return value?.let {
            val converted = if (isUTC) it.atUtc() else value
            "'" + converted.format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): ZonedDateTime? {
        return toItem(record, name, false)
    }

    fun toItem(record: Record, name: String, isUTC: Boolean = false): ZonedDateTime? {
        return if (isUTC)
            record.getZonedDateTimeUtc(name)
        else
            record.getZonedDateTime(name)
    }
}
