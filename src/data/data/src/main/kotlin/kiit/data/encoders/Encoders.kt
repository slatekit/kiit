package kiit.data.encoders


/**
 * Stores all the encoders for all supported data types
 */
open class Encoders<TId, T>(val utc:Boolean = true) where TId: kotlin.Comparable<TId>, T:Any {
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
    open val zonedDateTimes     = ZonedDateTimeEncoder(utc = utc)
    open val dateTimes          = DateTimeEncoder(utc = utc)
    open val instants           = InstantEncoder()
    open val uuids              = UUIDEncoder()
    open val upids              = UPIDEncoder()
    open val enums              = EnumEncoder()
}
