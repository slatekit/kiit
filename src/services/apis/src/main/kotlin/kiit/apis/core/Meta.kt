package kiit.apis.core

import org.json.simple.JSONObject
import kiit.apis.support.JsonSupport
import slatekit.common.*
import slatekit.common.crypto.Encryptor
// import java.time.*
import org.threeten.bp.*
import slatekit.common.convert.Conversions
import slatekit.common.values.Metadata

/**
 * Used to represent a request that originates from a json file.
 * This is useful for automation purposes and replaying an api action from a file source.
 * @param rawSource : Raw source
 * @param json : JSON object
 * @param enc : Encryptor
 */
data class Meta(
    val rawSource: Any,
    val json: JSONObject,
    val enc: Encryptor?
) : Metadata, JsonSupport {
    override val raw: Any = json
    override fun toJson(): JSONObject = json

    override fun toMap(): Map<String, Any> {
        val pairs = json.keys.map { key -> Pair(key?.toString() ?: "", json.get(key) ?: "") }
        return pairs.toMap()
    }

    override fun size(): Int = json.size
    override fun get(key: String): Any? = getStringRaw(key)
    // override fun getObject(key: String): Any? = getStringRaw(key)
    override fun containsKey(key: String): Boolean = json.containsKey(key)

    override fun getString(key: String): String = Strings.decrypt(getStringRaw(key).trim()) { it -> enc?.decrypt(it) ?: it }
    override fun getBool(key: String): Boolean = Conversions.toBool(getStringRaw(key))
    override fun getShort(key: String): Short = Conversions.toShort(getStringRaw(key))
    override fun getInt(key: String): Int = Conversions.toInt(getStringRaw(key))
    override fun getLong(key: String): Long = Conversions.toLong(getStringRaw(key))
    override fun getFloat(key: String): Float = Conversions.toFloat(getStringRaw(key))
    override fun getDouble(key: String): Double = Conversions.toDouble(getStringRaw(key))
    override fun getInstant(key: String): Instant = Conversions.toInstant(getStringRaw(key))
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(getStringRaw(key))
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(getStringRaw(key))
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(getStringRaw(key))
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(getStringRaw(key))
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(getStringRaw(key))
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = Conversions.toZonedDateTimeUtc(getStringRaw(key))

    private fun getInternalString(key: String): String {
        return if (containsKey(key)) {
            json.get(key) as String
        } else {
            ""
        }
    }

    private fun getStringRaw(key: String): String = getInternalString(key).trim()
}
