package slatekit.data.encoders

import slatekit.common.Record
import slatekit.common.data.Encoding
import slatekit.common.ext.orElse
import slatekit.data.Consts
import java.util.*


open class Encoders<TId, T> where TId: kotlin.Comparable<TId>, T:Any {
    open val bools              = BoolEncoder()
    open val strings            = StringEncoder()
    open val shorts             = ShortEncoder()
    open val ints               = IntEncoder()
    open val longs              = LongEncoder()
    open val floats             = FloatEncoder()
    open val doubles            = DoubleEncoder()
    open val localDates         = LocalDateEncoder()
    open val localTimes         = LocalTimeEncoder()
    open val localDateTimes     = LocalDateTimeEncoder()
    open val zonedDateTimes     = ZonedDateTimeEncoder()
    open val dateTimes          = DateTimeEncoder()
    open val instants           = InstantEncoder()
    open val uuids              = UUIDEncoder()
    open val uniqueIds          = UPIDEncoder()
    open val enums              = EnumEncoder()
}

open class Encoders2<TId, T> where TId: kotlin.Comparable<TId>, T:Any {
    open val bools    : (Boolean) -> String  = { value: Boolean? -> value?.let { if (value) "1" else "0" } ?: Consts.NULL }
    open val strings  : (String)  -> String  = { value: String?  -> value?.let { "'" + Encoding.ensureValue(value.orElse("")) + "'" } ?: Consts.NULL }
    open val shorts   : (Short)   -> String  = { value: Short?   -> value?.toString() ?: Consts.NULL }
    open val ints     : (Int)     -> String  = { value: Int?     -> value?.toString() ?: Consts.NULL }
    open val longs    : (Long)    -> String  = { value: Long?    -> value?.toString() ?: Consts.NULL }
    open val floats   : (Float)   -> String  = { value: Float?   -> value?.toString() ?: Consts.NULL }
    open val doubles  : (Double)  -> String  = { value: Double?  -> value?.toString() ?: Consts.NULL }
    open val uuids    : (UUID)    -> String  = { value: UUID?    -> value?.let { "'$value'" } ?: Consts.NULL }
}


open class Decoders2<TId, T> where TId: kotlin.Comparable<TId>, T:Any {
    open val bools    : (Record, String) -> Boolean? = { record: Record, name: String  -> record.getBoolOrNull(name) }
    open val strings  : (Record, String) -> String?  = { record: Record, name: String  -> record.getStringOrNull(name) }
    open val shorts   : (Record, String) -> Short?   = { record: Record, name: String  -> record.getShortOrNull(name) }
    open val ints     : (Record, String) -> Int?     = { record: Record, name: String  -> record.getIntOrNull(name) }
    open val longs    : (Record, String) -> Long?    = { record: Record, name: String  -> record.getLongOrNull(name) }
    open val floats   : (Record, String) -> Float?   = { record: Record, name: String  -> record.getFloatOrNull(name) }
    open val doubles  : (Record, String) -> Double?  = { record: Record, name: String  -> record.getDoubleOrNull(name) }
    open val uuids    : (Record, String) -> UUID?    = { record: Record, name: String  -> record.getUUIDOrNull(name) }
}
