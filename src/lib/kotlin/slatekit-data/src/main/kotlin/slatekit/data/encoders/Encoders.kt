package slatekit.data.encoders

import org.threeten.bp.*
import slatekit.common.DateTime
import slatekit.common.Record
import slatekit.common.data.Encoding
import slatekit.common.ext.orElse
import slatekit.common.ids.UPID
import slatekit.data.Consts
import java.util.*


abstract class EncodersBase {
    protected val internalLocalDates         = LocalDateEncoder()
    protected val internalLocalTimes         = LocalTimeEncoder()
    protected val internalLocalDateTimes     = LocalDateTimeEncoder()
    protected val internalZonedDateTimes     = ZonedDateTimeEncoder()
    protected val internalDateTimes          = DateTimeEncoder()
    protected val internalInstants           = InstantEncoder()
}

open class Encoders<TId, T> : EncodersBase() where TId: kotlin.Comparable<TId>, T:Any {
    open val bools          : (Boolean?      ) -> String  = { value: Boolean?      -> value?.let { if (value) "1" else "0" } ?: Consts.NULL }
    open val strings        : (String?       ) -> String  = { value: String?       -> value?.let { "'" + Encoding.ensureValue(value.orElse("")) + "'" } ?: Consts.NULL }
    open val shorts         : (Short?        ) -> String  = { value: Short?        -> value?.toString() ?: Consts.NULL }
    open val ints           : (Int?          ) -> String  = { value: Int?          -> value?.toString() ?: Consts.NULL }
    open val longs          : (Long?         ) -> String  = { value: Long?         -> value?.toString() ?: Consts.NULL }
    open val floats         : (Float?        ) -> String  = { value: Float?        -> value?.toString() ?: Consts.NULL }
    open val doubles        : (Double?       ) -> String  = { value: Double?       -> value?.toString() ?: Consts.NULL }
    open val uuids          : (UUID?         ) -> String  = { value: UUID?         -> value?.let { "'$value'" } ?: Consts.NULL }
    open val upids          : (UPID?         ) -> String  = { value: UPID?         -> value?.let { "'${value.value}'" } ?: Consts.NULL }
    open val localDates     : (LocalDate?    ) -> String = { value: LocalDate?     -> internalLocalDates.encode(value)}
    open val localTimes     : (LocalTime?    ) -> String = { value: LocalTime?     -> internalLocalTimes.encode(value)}
    open val localDateTimes : (LocalDateTime?) -> String = { value: LocalDateTime? -> internalLocalDateTimes.encode(value)}
    open val zonedDateTimes : (ZonedDateTime?) -> String = { value: ZonedDateTime? -> internalZonedDateTimes.encode(value)}
    open val dateTimes      : (DateTime?     ) -> String = { value: DateTime?      -> internalDateTimes.encode(value)}
    open val instants       : (Instant?      ) -> String = { value: Instant?       -> internalInstants.encode(value)}
}


open class Decoders<TId, T> : EncodersBase() where TId: kotlin.Comparable<TId>, T:Any {
    // bools : Function(record:Record, name:String) : Boolean     = { ....  }
    open val bools           : (Record, String) -> Boolean?       = { record: Record, name: String  -> record.getBoolOrNull(name) }
    open val strings         : (Record, String) -> String?        = { record: Record, name: String  -> record.getStringOrNull(name) }
    open val shorts          : (Record, String) -> Short?         = { record: Record, name: String  -> record.getShortOrNull(name) }
    open val ints            : (Record, String) -> Int?           = { record: Record, name: String  -> record.getIntOrNull(name) }
    open val longs           : (Record, String) -> Long?          = { record: Record, name: String  -> record.getLongOrNull(name) }
    open val floats          : (Record, String) -> Float?         = { record: Record, name: String  -> record.getFloatOrNull(name) }
    open val doubles         : (Record, String) -> Double?        = { record: Record, name: String  -> record.getDoubleOrNull(name) }
    open val uuids           : (Record, String) -> UUID?          = { record: Record, name: String  -> record.getUUIDOrNull(name) }
    open val upids           : (Record, String) -> UPID?          = { record: Record, name: String  -> record.getUPIDOrNull(name) }
    open val localDates      : (Record, String) -> LocalDate?     = { record: Record, name: String -> internalLocalDates.decode(record, name) }
    open val localTimes      : (Record, String) -> LocalTime?     = { record: Record, name: String -> internalLocalTimes.decode(record, name)}
    open val localDateTimes  : (Record, String) -> LocalDateTime? = { record: Record, name: String -> internalLocalDateTimes.decode(record, name)}
    open val zonedDateTimes  : (Record, String) -> ZonedDateTime? = { record: Record, name: String -> internalZonedDateTimes.decode(record, name)}
    open val dateTimes       : (Record, String) -> DateTime?      = { record: Record, name: String -> internalDateTimes.decode(record, name)}
    open val instants        : (Record, String) -> Instant?       = { record: Record, name: String -> internalInstants.decode(record, name)}
}
