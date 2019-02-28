package slatekit.orm.databases

import slatekit.orm.databases.converters.*
import slatekit.orm.databases.statements.Delete
import slatekit.orm.databases.statements.Insert
import slatekit.orm.databases.statements.Select
import slatekit.orm.databases.statements.Update


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
    open val localDateTimes     = LocalDateTimeConverter
    open val zonedDateTimes     = ZonedDateTimeConverter
    open val dateTimes          = DateTimeConverter
    open val instants           = InstantConverter
    open val uuids              = UUIDConverter
    open val uniqueIds          = UniqueIdConverter
    open val enums              = EnumConverter

    open val inserts = Insert()
    open val updates = Update()
    open val deletes = Delete()
    open val selects = Select()
}