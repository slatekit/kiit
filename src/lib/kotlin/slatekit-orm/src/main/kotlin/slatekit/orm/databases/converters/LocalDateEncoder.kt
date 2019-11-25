package slatekit.orm.databases.converters

import slatekit.orm.core.SqlEncoder
import slatekit.common.Record
import slatekit.orm.Consts
//import java.time.*
import org.threeten.bp.*

object LocalDateEncoder : SqlEncoder<LocalDate> {

    override fun encode(value: LocalDate?): String {
        return value?.let { "'" + value.format(Consts.dateFormat) + "'" } ?: Consts.NULL
    }

    override fun decode(record: Record, name: String): LocalDate? {
        return record.getLocalDate(name)
    }
}