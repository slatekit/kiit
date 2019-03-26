package slatekit.common

import org.threeten.bp.*
import slatekit.common.ids.UniqueId
import java.util.*

interface Gets {

    fun getString(key: String): String
    fun getBool(key: String): Boolean
    fun getShort(key: String): Short
    fun getInt(key: String): Int
    fun getLong(key: String): Long
    fun getFloat(key: String): Float
    fun getDouble(key: String): Double
    fun getInstant(key:String): Instant
    fun getDateTime(key: String): DateTime
    fun getLocalDate(key: String): LocalDate
    fun getLocalTime(key: String): LocalTime
    fun getLocalDateTime(key: String): LocalDateTime
    fun getZonedDateTime(key: String): ZonedDateTime
    fun getZonedDateTimeUtc(key: String): ZonedDateTime
    fun getUUID(key: String): java.util.UUID = UUID.fromString(getString(key))
    fun getUniqueId(key: String): UniqueId = UniqueId.fromString(getString(key))

    // Get values as Option[T]
    fun getStringOrNull(key: String): String?
    fun getBoolOrNull(key: String): Boolean?
    fun getShortOrNull(key: String): Short?
    fun getIntOrNull(key: String): Int?
    fun getLongOrNull(key: String): Long?
    fun getFloatOrNull(key: String): Float?
    fun getDoubleOrNull(key: String): Double?
    fun getInstantOrNull(key: String): Instant?
    fun getDateTimeOrNull(key: String): DateTime?
    fun getLocalDateOrNull(key: String): LocalDate?
    fun getLocalTimeOrNull(key: String): LocalTime?
    fun getLocalDateTimeOrNull(key: String): LocalDateTime?
    fun getZonedDateTimeOrNull(key: String): ZonedDateTime?
    fun getZonedDateTimeUtcOrNull(key: String): ZonedDateTime?
    fun getUUIDOrNull(key: String): UUID?
    fun getUniqueIdOrNull(key: String): UniqueId?
}