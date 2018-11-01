package slatekit.entities.databases


open class Converter {
    open val bools              = BoolConverter
    open val strings            = StringConverter
    open val shorts             = ShortConverter
    open val ints               = IntConverter
    open val longs              = LongConverter
    open val floats             = FloatConverter
    open val doubles            = DoubleConverter
    open val localDates         = LocalDateConverter
    open val localTimes         = LocalTimeConverter
    open val localDateTimes     = LocalDateTimeConverter(false)
    open val localDateTimesUtc  = LocalDateTimeConverter(true)
    open val zonedDateTimes     = ZonedDateTimeConverter(false)
    open val zonedDateTimesUtc  = ZonedDateTimeConverter(true)
    open val dateTimes          = DateTimeConverter(false)
    open val dateTimesUtc       = DateTimeConverter(true)
    open val instants           = InstantConverter
    open val uuids              = UUIDConverter
    open val uniqueIds          = UniqueIdConverter
}