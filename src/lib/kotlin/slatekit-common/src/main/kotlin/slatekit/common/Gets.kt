package slatekit.common

import org.threeten.bp.*
import slatekit.common.ids.UPID
import slatekit.common.ids.UPIDs
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
    fun getUPID(key: String): UPID = UPIDs.parse(getString(key))

    // Get values as Option[T]
    fun getStringOrNull(key: String): String? = getOrNull(key) { k: String -> getString(k) }
    fun getBoolOrNull(key: String): Boolean? = getOrNull(key) { k: String -> getBool(k) }
    fun getShortOrNull(key: String): Short? = getOrNull(key) { k: String -> getShort(k) }
    fun getIntOrNull(key: String): Int? = getOrNull(key) { k: String -> getInt(k) }
    fun getLongOrNull(key: String): Long? = getOrNull(key) { k: String -> getLong(k) }
    fun getFloatOrNull(key: String): Float? = getOrNull(key) { k: String -> getFloat(k) }
    fun getDoubleOrNull(key: String): Double? = getOrNull(key) { k: String -> getDouble(k) }
    fun getInstantOrNull(key: String): Instant? = getOrNull(key) { k: String -> getInstant(k) }
    fun getDateTimeOrNull(key: String): DateTime? = getOrNull(key) { k: String -> getDateTime(k) }
    fun getLocalDateOrNull(key: String): LocalDate? = getOrNull(key) { k: String -> getLocalDate(k) }
    fun getLocalTimeOrNull(key: String): LocalTime? = getOrNull(key) { k: String -> getLocalTime(k) }
    fun getLocalDateTimeOrNull(key: String): LocalDateTime? = getOrNull(key) { k: String -> getLocalDateTime(k) }
    fun getZonedDateTimeOrNull(key: String): ZonedDateTime? = getOrNull(key) { k: String -> getZonedDateTime(k) }
    fun getZonedDateTimeUtcOrNull(key: String): ZonedDateTime? = getOrNull(key) { k: String -> getZonedDateTimeUtc(k) }
    fun getUUIDOrNull(key: String): UUID? = getOrNull(key) { k: String -> UUID.fromString(getString(k)) }
    fun getUPIDOrNull(key: String): UPID? = getOrNull(key) { k: String -> UPIDs.parse(getString(k)) }


    // Get value or default
    fun getStringOrElse(key: String, default: String): String = getOrElse(key, { k: String -> getString(k) }, default)
    fun getBoolOrElse(key: String, default: Boolean): Boolean = getOrElse(key, { k: String -> getBool(k) }, default)
    fun getShortOrElse(key: String, default: Short): Short = getOrElse(key, { k: String -> getShort(k) }, default)
    fun getIntOrElse(key: String, default: Int): Int = getOrElse(key, { k: String -> getInt(k) }, default)
    fun getLongOrElse(key: String, default: Long): Long = getOrElse(key, { k: String -> getLong(k) }, default)
    fun getFloatOrElse(key: String, default: Float): Float = getOrElse(key, { k: String -> getFloat(k) }, default)
    fun getDoubleOrElse(key: String, default: Double): Double = getOrElse(key, { k: String -> getDouble(k) }, default)
    fun getInstantOrElse(key: String, default: Instant): Instant = getOrElse(key, { k: String -> getInstant(k) }, default)
    fun getDateTimeOrElse(key: String, default: DateTime): DateTime = getOrElse(key, { k: String -> getDateTime(k) }, default)
    fun getLocalDateOrElse(key: String, default: LocalDate): LocalDate = getOrElse(key, { k: String -> getLocalDate(k) }, default)
    fun getLocalTimeOrElse(key: String, default: LocalTime): LocalTime = getOrElse(key, { k: String -> getLocalTime(k) }, default)
    fun getLocalDateTimeOrElse(key: String, default: LocalDateTime): LocalDateTime = getOrElse(key, { k: String -> getLocalDateTime(k) }, default)
    fun getZonedDateTimeOrElse(key: String, default: ZonedDateTime): ZonedDateTime = getOrElse(key, { k: String -> getZonedDateTime(k) }, default)
    fun getZonedDateTimeUtcOrElse(key: String, default: ZonedDateTime): ZonedDateTime = getOrElse(key, { k: String -> getZonedDateTimeUtc(k) }, default)
    fun getUUIDOrElse(key: String, default:UUID): UUID = getOrElse(key, { k: String -> UUID.fromString(getString(k)) }, default)
    fun getUPIDOrElse(key: String, default:UPID): UPID = getOrElse(key, { k: String -> UPIDs.parse(getString(k)) }, default)

    fun <T> getOrNull(key: String, fetcher: (String) -> T): T?
    fun <T> getOrElse(key: String, fetcher: (String) -> T, default: T): T
}