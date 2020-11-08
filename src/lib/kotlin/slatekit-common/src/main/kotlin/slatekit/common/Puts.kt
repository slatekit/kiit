package slatekit.common

import org.threeten.bp.*
import slatekit.common.ids.UPID
import java.util.*

interface Puts {
    fun putString(key: String, value: String)
    fun putBool(key: String, value: Boolean)
    fun putShort(key: String, value: Short)
    fun putInt(key: String, value: Int)
    fun putLong(key: String, value: Long)
    fun putFloat(key: String, value: Float)
    fun putDouble(key: String, value: Double)
    fun putInstant(key:String, value: Instant)
    fun putDateTime(key: String, value: DateTime)
    fun putLocalDate(key: String, value: LocalDate)
    fun putLocalTime(key: String, value: LocalTime)
    fun putLocalDateTime(key: String, value: LocalDateTime)
    fun putZonedDateTime(key: String, value: ZonedDateTime)
    fun putZonedDateTimeUtc(key: String, value: ZonedDateTime)
    fun putUUID(key: String, value: java.util.UUID)
    fun putUPID(key: String, value: UPID)

    // put values as Option[T]
    fun putStringOrNull(key: String, value: String?)
    fun putBoolOrNull(key: String, value: Boolean?)
    fun putShortOrNull(key: String, value: Short?)
    fun putIntOrNull(key: String, value: Int?)
    fun putLongOrNull(key: String, value: Long?)
    fun putFloatOrNull(key: String, value: Float?)
    fun putDoubleOrNull(key: String, value: Double?)
    fun putInstantOrNull(key: String, value: Instant?)
    fun putDateTimeOrNull(key: String, value: DateTime?)
    fun putLocalDateOrNull(key: String, value: LocalDate?)
    fun putLocalTimeOrNull(key: String, value: LocalTime?)
    fun putLocalDateTimeOrNull(key: String, value: LocalDateTime?)
    fun putZonedDateTimeOrNull(key: String, value: ZonedDateTime?)
    fun putZonedDateTimeUtcOrNull(key: String, value: ZonedDateTime?)
    fun putUUIDOrNull(key: String, value: UUID?)
    fun putUPIDOrNull(key: String, value: UPID?)
}