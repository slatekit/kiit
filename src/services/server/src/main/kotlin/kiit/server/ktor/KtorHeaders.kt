/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package kiit.server.ktor

import kiit.common.convert.Conversions
import kiit.common.DateTime
import kiit.common.crypto.Encryptor

import io.ktor.request.*
import org.json.simple.JSONObject
import kiit.common.values.Metadata
import kiit.common.Strings
//import java.time.*
import org.threeten.bp.*
import kiit.apis.support.JsonSupport

data class KtorHeaders(val req: ApplicationRequest, val enc: Encryptor?) : Metadata, JsonSupport {

    override val raw: Any = req.headers


    /**
     * Convertible back to JSON for queueing
     */
    override fun toJson(): JSONObject {
        val root = JSONObject()
        req.headers.names().forEach{ name ->
            root[name] = req.headers.get(name)
        }
        return root
    }


    /**
     * Convertible to a Map for processing
     */
    override fun toMap(): Map<String, Any> {
        val pairs = req.headers.names().map { name -> Pair<String, Any>(name, req.headers.get(name) ?: "") }.toMap()
        return pairs.toMap()
    }

    override fun get(key: String): Any? = getInternal(key)
    //override fun getObject(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = req.headers.contains(key)
    override fun size(): Int = req.headers.names().size

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

    fun getInternal(key: String): Any? {
        return if (containsKey(key)) {
            val value = req.headers[key]
            value?.trim() ?: value
        } else {
            null
        }
    }

    fun getInternalString(key: String): String {
        return if (containsKey(key)) {
            val value = req.headers[key]
            value?.trim() ?: ""
        } else {
            ""
        }
    }

    fun getStringRaw(key: String): String = getInternalString(key).trim()
}
