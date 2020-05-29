package slatekit.meta

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.threeten.bp.*
import slatekit.common.*
import slatekit.common.crypto.Encryptor

/**
 * Used to represent a request that originate from a json file.
 * This is useful for automation purposes and replaying an api action from a file source.
 * @param json : The JSON object
 * @param enc : The encryptor
 */
data class InputsJSON(
        val rawSource: Any,
        val enc: Encryptor?,
        val json: JSONObject
) : Inputs {

    override fun get(key: String): Any? = getInternal(key)
    override fun size(): Int = json.size

    override val raw: Any = json
    override fun getString(key: String): String = Strings.decrypt(getInternalString(key).trim()) { it -> enc?.decrypt(it) ?: it }
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

    override fun containsKey(key: String): Boolean {
        return json.containsKey(key)
    }

    fun getInternal(key: String): Any? {
        val value = if (json.containsKey(key)) {
            json.get(key)
        } else {
            null
        }
        return value
    }

    fun getInternalString(key: String): String {
        val value = if (json.containsKey(key)) {
            json.get(key) ?: ""
        } else {
            ""
        }
        return value.toString()
    }

    fun getStringRaw(key: String): String = getInternalString(key).trim()


    companion object {

        fun of(text:String, enc:Encryptor? = null):InputsJSON {
            val parser = JSONParser()
            val json = parser.parse(text) as JSONObject
            return InputsJSON(text, enc, json)
        }
    }
}