package slatekit.orm.core

import slatekit.orm.databases.converters.*
import slatekit.orm.databases.statements.Delete
import slatekit.orm.databases.statements.Insert
import slatekit.orm.databases.statements.Select
import slatekit.orm.databases.statements.Update


open class Converter<TId, T> where TId: kotlin.Comparable<TId>, T:Any {
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

    open val inserts = Insert<TId, T>()
    open val updates = Update<TId, T>()
    open val deletes = Delete<TId, T>()
    open val selects = Select<TId, T>()
}