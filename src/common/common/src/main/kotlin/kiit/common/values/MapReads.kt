package kiit.common.values

import org.threeten.bp.*
import kiit.common.convert.Conversions
import kiit.common.Strings
import kiit.common.ext.atUtc

/**
 * Simple In-Memory updatable settings ( used for mostly testing ) purposes
 */
class MapReads(private val _data:Map<String, Any?> = mapOf()) : Gets {

    fun size():Int = _data.size
    fun get(key: String): Any? = _data.get(key)
    fun containsKey(key:String):Boolean = _data.containsKey(key)

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

    override fun <T> getOrNull(key: String, fetcher: (String) -> T): T? {
        return if (containsKey(key)) {
            val v = get(key)
            v?.let { fetcher(key) }
        } else {
            null
        }
    }

    override fun <T> getOrElse(key: String, fetcher: (String) -> T, default: T): T =
        if (containsKey(key)) fetcher(key) else default

    private fun getStringRaw(key: String): String = _data.get(key)?.toString()?.trim() ?: ""
}