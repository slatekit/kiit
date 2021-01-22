package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts
//import java.time.*
import org.threeten.bp.*

class LocalTimeEncoder : SqlEncoder<LocalTime> {

    override fun encode(value: LocalTime?): String {
        return value?.let { "'" + value.format(Consts.timeFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalTime? {
        return record.getLocalTime(name)
    }
}
