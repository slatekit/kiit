package slatekit.entities.databases.mysql

object MySqlConverter {
    val bools              = BoolConverter
    val strings            = StringConverter
    val shorts             = ShortConverter
    val ints               = IntConverter
    val longs              = LongConverter
    val floats             = FloatConverter
    val doubles            = DoubleConverter
    val localDates         = LocalDateConverter
    val localTimes         = LocalTimeConverter
    val localDateTimes     = LocalDateTimeConverter(false)
    val localDateTimesUtc  = LocalDateTimeConverter(true)
    val zonedDateTimes     = ZonedDateTimeConverter(false)
    val zonedDateTimesUtc  = ZonedDateTimeConverter(true)
    val dateTimes          = DateTimeConverter(false)
    val dateTimesUtc       = DateTimeConverter(true)
    val instants           = InstantConverter
    val uuids              = UUIDConverter
    val uniqueIds          = UniqueIdConverter
}