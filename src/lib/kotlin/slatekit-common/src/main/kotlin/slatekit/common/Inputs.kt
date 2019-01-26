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

package slatekit.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

interface InputsUpdateable {
    // Immutable add
    fun add(key: String, value: Any): Inputs
}

/**
 * Base class to support retrieving inputs form multiple sources:
 * 1. command line arguments
 * 2. config settings
 * 3. http requests
 * 4. in-memory settings
 *
 * NOTE: This allows for abstracting the input source to accommodate
 * Slate Kit protocol independent APIs
 */
interface Inputs {

    // Get values for core value types, must be implemented in derived classes
    val raw: Any

    fun get(key: String): Any?
    fun containsKey(key: String): Boolean
    fun size(): Int

    fun getString(key: String): String
    fun getBool(key: String): Boolean
    fun getShort(key: String): Short
    fun getInt(key: String): Int
    fun getLong(key: String): Long
    fun getFloat(key: String): Float
    fun getDouble(key: String): Double
    fun getDateTime(key: String): DateTime
    fun getLocalDate(key: String): LocalDate
    fun getLocalTime(key: String): LocalTime
    fun getLocalDateTime(key: String): LocalDateTime
    fun getZonedDateTime(key: String): ZonedDateTime

    // Get values as Option[T]
    fun getStringOrNull(key: String): String? = getOrNull(key) { k: String -> getString(k) }
    fun getBoolOrNull(key: String): Boolean? = getOrNull(key) { k: String -> getBool(k) }
    fun getShortOrNull(key: String): Short? = getOrNull(key) { k: String -> getShort(k) }
    fun getIntOrNull(key: String): Int? = getOrNull(key) { k: String -> getInt(k) }
    fun getLongOrNull(key: String): Long? = getOrNull(key) { k: String -> getLong(k) }
    fun getFloatOrNull(key: String): Float? = getOrNull(key) { k: String -> getFloat(k) }
    fun getDoubleOrNull(key: String): Double? = getOrNull(key) { k: String -> getDouble(k) }
    fun getDateTimeOrNull(key: String): DateTime? = getOrNull(key) { k: String -> getDateTime(k) }
    fun getLocalDateOrNull(key: String): LocalDate? = getOrNull(key) { k: String -> getLocalDate(k) }
    fun getLocalTimeOrNull(key: String): LocalTime? = getOrNull(key) { k: String -> getLocalTime(k) }
    fun getLocalDateTimeOrNull(key: String): LocalDateTime? = getOrNull(key) { k: String -> getLocalDateTime(k) }
    fun getZonedDateTimeOrNull(key: String): ZonedDateTime? = getOrNull(key) { k: String -> getZonedDateTime(k) }

    // Get value or default
    fun getStringOrElse(key: String, default: String): String = getOrElse(key, { k: String -> getString(k) }, default)
    fun getBoolOrElse(key: String, default: Boolean): Boolean = getOrElse(key, { k: String -> getBool(k) }, default)
    fun getShortOrElse(key: String, default: Short): Short = getOrElse(key, { k: String -> getShort(k) }, default)
    fun getIntOrElse(key: String, default: Int): Int = getOrElse(key, { k: String -> getInt(k) }, default)
    fun getLongOrElse(key: String, default: Long): Long = getOrElse(key, { k: String -> getLong(k) }, default)
    fun getFloatOrElse(key: String, default: Float): Float = getOrElse(key, { k: String -> getFloat(k) }, default)
    fun getDoubleOrElse(key: String, default: Double): Double = getOrElse(key, { k: String -> getDouble(k) }, default)
    fun getDateTimeOrElse(key: String, default: DateTime): DateTime = getOrElse(key, { k: String -> getDateTime(k) }, default)
    fun getLocalDateOrElse(key: String, default: LocalDate): LocalDate = getOrElse(key, { k: String -> getLocalDate(k) }, default)
    fun getLocalTimeOrElse(key: String, default: LocalTime): LocalTime = getOrElse(key, { k: String -> getLocalTime(k) }, default)
    fun getLocalDateTimeOrElse(key: String, default: LocalDateTime): LocalDateTime = getOrElse(key, { k: String -> getLocalDateTime(k) }, default)
    fun getZonedDateTimeOrElse(key: String, default: ZonedDateTime): ZonedDateTime = getOrElse(key, { k: String -> getZonedDateTime(k) }, default)

    // Get list and maps
    /**
     * gets a list of items of the type supplied.
     * NOTE: derived classes should override this. e.g. HttpInputs in slatekit.server
     * @param key
     * @param tpe
     * @return
     */
    fun getList(key: String, tpe: Class<*>): List<Any> {
        val converter = Conversions.converterFor(tpe)
        val input = get(key)
        return input?.let { inputVal ->

            val result = when (inputVal) {
                "null" -> listOf()
                "\"\"" -> listOf()
                is String -> inputVal.toString().split(',').toList().map(converter)
                is List<*> -> (input as List<*>).map { it as Any }
                else -> listOf()
            }
            result
        } ?: listOf()
    }

    /**
     * gets a map of items of the type supplied.
     * NOTE: derived classes should override this. e.g. HttpInputs in slatekit.server
     * @param key
     * @return
     */
    fun getMap(key: String, tpeKey: Class<*>, tpeVal: Class<*>): Map<*, *> {
        val keyConverter = Conversions.converterFor(tpeKey)
        val valConverter = Conversions.converterFor(tpeVal)
        val input = get(key)
        val emptyMap = mapOf<Any, Any>()
        return input?.let { inputVal ->

            val result = when (inputVal) {
                "null"       -> emptyMap
                "\"\""       -> emptyMap
                is String    -> inputVal.toString().splitToMapOfType(',', true, '=', keyConverter, valConverter)
                is Map<*, *> -> inputVal
                else -> emptyMap
            }
            result
        } ?: mapOf<Any, Any>()
    }

    // Helpers
    fun <T> getOrNull(key: String, fetcher: (String) -> T): T? = if (containsKey(key)) fetcher(key) else null

    fun <T> getOrElse(key: String, fetcher: (String) -> T, default: T): T = if (containsKey(key)) fetcher(key) else default
}