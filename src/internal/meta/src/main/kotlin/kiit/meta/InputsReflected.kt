package kiit.meta

import org.threeten.bp.*
import slatekit.common.*
import slatekit.common.crypto.Encryptor
import slatekit.common.ext.atUtc
import slatekit.common.values.Inputs
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

/**
 * Used to represent a request that originate from a json file.
 * This is useful for automation purposes and replaying an api action from a file source.
 * @param json : The JSON object
 * @param enc : The encryptor
 */
data class InputsReflected(
        val source: Any,
        val enc: Encryptor?,
        val cls:KClass<*>
) : Inputs {

    private val lookup = cls.memberProperties.map { Pair(it.name, it) }.toMap()

    override fun get(key: String): Any? = getInternal(key)
    override fun size(): Int = cls.declaredMemberProperties.size

    override val raw: Any = source
    override fun getString(key: String): String = getInternal(key) as String
    override fun getBool(key: String): Boolean = getInternal(key) as Boolean
    override fun getShort(key: String): Short = getInternal(key) as Short
    override fun getInt(key: String): Int = getInternal(key) as Int
    override fun getLong(key: String): Long = getInternal(key) as Long
    override fun getFloat(key: String): Float = getInternal(key) as Float
    override fun getDouble(key: String): Double = getInternal(key) as Double
    override fun getInstant(key: String): Instant = getInternal(key) as Instant
    override fun getDateTime(key: String): DateTime = getInternal(key) as DateTime
    override fun getLocalDate(key: String): LocalDate = getInternal(key) as LocalDate
    override fun getLocalTime(key: String): LocalTime = getInternal(key) as LocalTime
    override fun getLocalDateTime(key: String): LocalDateTime = getInternal(key) as LocalDateTime
    override fun getZonedDateTime(key: String): ZonedDateTime = getInternal(key) as ZonedDateTime
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = (getInternal(key) as ZonedDateTime).atUtc()

    override fun containsKey(key: String): Boolean {
        return lookup.containsKey(key)
    }

    fun getInternal(key: String): Any? {
        val value = if (lookup.containsKey(key)) {
            val prop = lookup.get(key)
            prop?.getter?.call(source)
        } else {
            null
        }
        return value
    }
}