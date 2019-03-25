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

import slatekit.common.ids.UniqueId
//import java.time.*
import org.threeten.bp.*
import java.util.*

interface InputsUpdateable {
    // Immutable add
    fun add(key: String, value: Any): Inputs
}

/**
 * Interface to support reading inputs from multiple sources:
 * 1. command line arguments
 * 2. config settings
 * 3. http requests
 * 4. in-memory settings
 *
 * NOTE: This allows for abstracting the input source to accommodate
 * Slate Kit protocol independent APIs
 */
interface Inputs : Gets {

    // Get values for core value types, must be implemented in derived classes
    val raw: Any

    fun get(key: String): Any?
    fun containsKey(key: String): Boolean
    fun size(): Int

    // Get values as Option[T]
    override fun getStringOrNull(key: String): String? = getOrNull(key) { k: String -> getString(k) }
    override fun getBoolOrNull(key: String): Boolean? = getOrNull(key) { k: String -> getBool(k) }
    override fun getShortOrNull(key: String): Short? = getOrNull(key) { k: String -> getShort(k) }
    override fun getIntOrNull(key: String): Int? = getOrNull(key) { k: String -> getInt(k) }
    override fun getLongOrNull(key: String): Long? = getOrNull(key) { k: String -> getLong(k) }
    override fun getFloatOrNull(key: String): Float? = getOrNull(key) { k: String -> getFloat(k) }
    override fun getDoubleOrNull(key: String): Double? = getOrNull(key) { k: String -> getDouble(k) }
    override fun getInstantOrNull(key: String): Instant? = getOrNull(key) { k: String -> getInstant(k) }
    override fun getDateTimeOrNull(key: String): DateTime? = getOrNull(key) { k: String -> getDateTime(k) }
    override fun getLocalDateOrNull(key: String): LocalDate? = getOrNull(key) { k: String -> getLocalDate(k) }
    override fun getLocalTimeOrNull(key: String): LocalTime? = getOrNull(key) { k: String -> getLocalTime(k) }
    override fun getLocalDateTimeOrNull(key: String): LocalDateTime? = getOrNull(key) { k: String -> getLocalDateTime(k) }
    override fun getZonedDateTimeOrNull(key: String): ZonedDateTime? = getOrNull(key) { k: String -> getZonedDateTime(k) }
    override fun getZonedDateTimeUtcOrNull(key: String): ZonedDateTime? = getOrNull(key) { k: String -> getZonedDateTimeUtc(k) }
    override fun getUUIDOrNull(key: String): UUID? = getOrNull(key) { k: String -> UUID.fromString(getString(k)) }
    override fun getUniqueIdOrNull(key: String): UniqueId? = getOrNull(key) { k: String -> UniqueId.fromString(getString(k)) }

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
    fun getUniqueIdOrElse(key: String, default:UniqueId): UniqueId = getOrElse(key, { k: String -> UniqueId.fromString(getString(k)) }, default)

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