package slatekit.common.conf

import org.threeten.bp.*
import slatekit.common.Conversions
import slatekit.common.DateTime
import slatekit.common.Internal
import slatekit.common.Strings
import slatekit.common.ext.atUtc
import slatekit.common.ids.UPID
import java.util.*

/**
 * Simple In-Memory updatable settings ( used for mostly testing ) purposes
 */
@Internal("NON-PRODUCTION USAGE: Used for internal use, unit-tests, prototyping only")
class MapSettings(private val _data:MutableMap<String, Any?> = mutableMapOf()) : Settings {

    override fun init() {
    }

    override fun done() {
    }

    override val raw:Any = _data
    override fun size():Int = _data.size
    override fun get(key: String): Any? = _data.get(key)
    override fun containsKey(key:String):Boolean = _data.containsKey(key)

    // NON-PRODUCTION USAGE - See above
    override fun getString(key: String): String = Strings.decrypt(getStringRaw(key), null)
    override fun getBool(key: String): Boolean = Conversions.toBool(getStringRaw(key))
    override fun getShort(key: String): Short = Conversions.toShort(getStringRaw(key))
    override fun getInt(key: String): Int = Conversions.toInt(getStringRaw(key))
    override fun getLong(key: String): Long = Conversions.toLong(getStringRaw(key))
    override fun getFloat(key: String): Float = Conversions.toFloat(getStringRaw(key))
    override fun getDouble(key: String): Double = Conversions.toDouble(getStringRaw(key))
    override fun getDateTime(key: String): ZonedDateTime = ZonedDateTime.parse(key)
    override fun getInstant(key: String): Instant = Instant.parse(getStringRaw(key))
    override fun getLocalDate(key: String): LocalDate = LocalDate.parse(getStringRaw(key))
    override fun getLocalDateTime(key: String): LocalDateTime = LocalDateTime.parse(getStringRaw(key))
    override fun getLocalTime(key: String): LocalTime = LocalTime.parse(getStringRaw(key))
    override fun getZonedDateTime(key: String): ZonedDateTime = ZonedDateTime.parse(getStringRaw(key))
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = ZonedDateTime.parse(getStringRaw(key)).atUtc()

    override fun putString(key: String, value: String) = putStringOrNull(key, value)
    override fun putBool(key: String, value: Boolean)  = putBoolOrNull(key, value)
    override fun putShort(key: String, value: Short) = putShortOrNull(key, value)
    override fun putInt(key: String, value: Int) = putIntOrNull(key, value)
    override fun putLong(key: String, value: Long) = putLongOrNull(key, value)
    override fun putFloat(key: String, value: Float) = putFloatOrNull(key, value)
    override fun putDouble(key: String, value: Double) = putDoubleOrNull(key, value)
    override fun putInstant(key: String, value: Instant) = putInstantOrNull(key, value)
    override fun putDateTime(key: String, value: DateTime) = putDateTimeOrNull(key, value)
    override fun putLocalDate(key: String, value: LocalDate) = putLocalDateOrNull(key, value)
    override fun putLocalTime(key: String, value: LocalTime) = putLocalTimeOrNull(key, value)
    override fun putLocalDateTime(key: String, value: LocalDateTime) = putLocalDateTimeOrNull(key, value)
    override fun putZonedDateTime(key: String, value: ZonedDateTime) = putZonedDateTimeOrNull(key, value)
    override fun putZonedDateTimeUtc(key: String, value: ZonedDateTime) = putZonedDateTimeOrNull(key, value)
    override fun putUUID(key: String, value: UUID) = putUUIDOrNull(key, value)
    override fun putUPID(key: String, value: UPID) = putUPIDOrNull(key, value)

    override fun putStringOrNull(key: String, value: String?) { _data[key] = value }
    override fun putBoolOrNull(key: String, value: Boolean?) { _data.put(key, value) }
    override fun putShortOrNull(key: String, value: Short?) { _data.put(key, value) }
    override fun putIntOrNull(key: String, value: Int?) { _data.put(key, value) }
    override fun putLongOrNull(key: String, value: Long?) { _data.put(key, value) }
    override fun putFloatOrNull(key: String, value: Float?) { _data.put(key, value) }
    override fun putDoubleOrNull(key: String, value: Double?) { _data.put(key, value) }
    override fun putInstantOrNull(key: String, value: Instant?) { _data.put(key, value) }
    override fun putDateTimeOrNull(key: String, value: DateTime?) { _data.put(key, value) }
    override fun putLocalDateOrNull(key: String, value: LocalDate?) { _data.put(key, value) }
    override fun putLocalTimeOrNull(key: String, value: LocalTime?) { _data.put(key, value) }
    override fun putLocalDateTimeOrNull(key: String, value: LocalDateTime?) { _data.put(key, value) }
    override fun putZonedDateTimeOrNull(key: String, value: ZonedDateTime?) { _data.put(key, value) }
    override fun putZonedDateTimeUtcOrNull(key: String, value: ZonedDateTime?) { _data.put(key, value) }
    override fun putUUIDOrNull(key: String, value: UUID?) { _data.put(key, value) }
    override fun putUPIDOrNull(key: String, value: UPID?) { _data.put(key, value) }


    private fun getStringRaw(key: String): String = _data.get(key)?.toString()?.trim() ?: ""
}