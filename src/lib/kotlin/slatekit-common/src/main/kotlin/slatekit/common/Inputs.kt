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

    fun getString(key: String): String
    fun getBool(key: String): Boolean
    fun getShort(key: String): Short
    fun getInt(key: String): Int
    fun getLong(key: String): Long
    fun getFloat(key: String): Float
    fun getDouble(key: String): Double
    fun getLocalDate(key: String): LocalDate
    fun getLocalTime(key: String): LocalTime
    fun getLocalDateTime(key: String): LocalDateTime
    fun getZonedDateTime(key: String): ZonedDateTime
    fun getDateTime(key: String): DateTime

    fun get(key: String): Any?
    fun getObject(key: String): Any?
    fun containsKey(key: String): Boolean
    fun size(): Int

    // Get values as Option[T]
    fun getStringOpt(key: String): String? = getOpt(key, { k: String -> getString(k) })
    fun getBoolOpt(key: String): Boolean? = getOpt(key, { k: String -> getBool(k) })
    fun getShortOpt(key: String): Short? = getOpt(key, { k: String -> getShort(k) })
    fun getIntOpt(key: String): Int? = getOpt(key, { k: String -> getInt(k) })
    fun getLongOpt(key: String): Long? = getOpt(key, { k: String -> getLong(k) })
    fun getFloatOpt(key: String): Float? = getOpt(key, { k: String -> getFloat(k) })
    fun getDoubleOpt(key: String): Double? = getOpt(key, { k: String -> getDouble(k) })
    fun getLocalDateOpt(key: String): LocalDate? = getOpt(key, { k: String -> getLocalDate(k) })
    fun getLocalTimeOpt(key: String): LocalTime? = getOpt(key, { k: String -> getLocalTime(k) })
    fun getLocalDateTimeOpt(key: String): LocalDateTime? = getOpt(key, { k: String -> getLocalDateTime(k) })
    fun getDateTimeOpt(key: String): DateTime? = getOpt(key, { k: String -> getDateTime(k) })

    // Get value or default
    fun getStringOrElse(key: String, default: String): String = getOrElse<String>(key, { k: String -> getString(k) }, default)

    fun getBoolOrElse(key: String, default: Boolean): Boolean = getOrElse<Boolean>(key, { k: String -> getBool(k) }, default)
    fun getShortOrElse(key: String, default: Short): Short = getOrElse<Short>(key, { k: String -> getShort(k) }, default)
    fun getIntOrElse(key: String, default: Int): Int = getOrElse<Int>(key, { k: String -> getInt(k) }, default)
    fun getLongOrElse(key: String, default: Long): Long = getOrElse<Long>(key, { k: String -> getLong(k) }, default)
    fun getFloatOrElse(key: String, default: Float): Float = getOrElse<Float>(key, { k: String -> getFloat(k) }, default)
    fun getDoubleOrElse(key: String, default: Double): Double = getOrElse<Double>(key, { k: String -> getDouble(k) }, default)
    fun getLocalDateOrElse(key: String, default: LocalDate): LocalDate = getOrElse<LocalDate>(key, { k: String -> getLocalDate(k) }, default)
    fun getLocalTimeOrElse(key: String, default: LocalTime): LocalTime = getOrElse<LocalTime>(key, { k: String -> getLocalTime(k) }, default)
    fun getLocalDateTimeOrElse(key: String, default: LocalDateTime): LocalDateTime = getOrElse<LocalDateTime>(key, { k: String -> getLocalDateTime(k) }, default)
    fun getDateTimeOrElse(key: String, default: DateTime): DateTime = getOrElse<DateTime>(key, { k: String -> getDateTime(k) }, default)

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
        val result = input?.let { inputVal ->

            val result = when (inputVal) {
                "null" -> listOf()
                "\"\"" -> listOf()
                is String -> inputVal.toString().split(',').toList().map(converter)
                is List<*> -> (input as List<*>).map { it as Any }
                else -> listOf()
            }
            result
        } ?: listOf<Any>()
        return result
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
        val result = input?.let { inputVal ->

            val result = when (inputVal) {
                "null" -> emptyMap
                "\"\"" -> emptyMap
                is String -> inputVal.toString().splitToMapOfType(',', true, '=', keyConverter, valConverter)
                is Map<*, *> -> inputVal
                else -> emptyMap
            }
            result
        } ?: mapOf<Any, Any>()
        return result
    }

    // Helpers
    fun <T> getOpt(key: String, fetcher: (String) -> T): T? = if (containsKey(key)) fetcher(key) else null

    fun <T> getOrElse(key: String, fetcher: (String) -> T, default: T): T = if (containsKey(key)) fetcher(key) else default
}