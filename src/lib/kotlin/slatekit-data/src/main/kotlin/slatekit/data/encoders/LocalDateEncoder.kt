package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts
//import java.time.*
import org.threeten.bp.*

class LocalDateEncoder : SqlEncoder<LocalDate> {

    override fun encode(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}
