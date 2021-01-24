package slatekit.data.encoders

import org.threeten.bp.*
import slatekit.common.DateTime
import slatekit.common.Record
import slatekit.common.data.Encoding
import slatekit.common.ext.orElse
import slatekit.common.ids.UPID
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
    open val upids              = UPIDEncoder()
    open val enums              = EnumEncoder()
}
