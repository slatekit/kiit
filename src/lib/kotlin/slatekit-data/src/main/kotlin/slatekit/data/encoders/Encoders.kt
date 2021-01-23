package slatekit.data.encoders


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
