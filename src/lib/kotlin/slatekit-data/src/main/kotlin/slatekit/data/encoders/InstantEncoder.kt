package slatekit.data.encoders


import slatekit.common.Record
import slatekit.data.Consts
//import java.time.*
import org.threeten.bp.*

class InstantEncoder : SqlEncoder<Instant> {

    override fun encode(value: Instant?): String {
        return value?.let {
            "'" + LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(Consts.dateTimeFormat) + "'"
        } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): Instant? {
        return record.getInstant(name)
    }
}
