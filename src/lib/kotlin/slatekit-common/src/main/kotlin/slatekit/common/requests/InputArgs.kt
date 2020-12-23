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

package slatekit.common.requests

import slatekit.common.*
//import java.time.*
import org.threeten.bp.*
import slatekit.common.convert.Conversions

/**
 * Created by kishorereddy on 5/25/17.
 */
open class InputArgs(
        val map: Map<String, Any>,
        private val decryptor: ((String) -> String)? = null
) : Metadata, InputsUpdateable {

    override val raw: Any = map
    override fun toMap(): Map<String, Any> = map

    override fun getString(key: String): String = Strings.decrypt(map[key].toString(), decryptor)
    override fun getBool(key: String): Boolean = Conversions.toBool(map[key].toString())
    override fun getShort(key: String): Short = Conversions.toShort(map[key].toString())
    override fun getInt(key: String): Int = Conversions.toInt(map[key].toString())
    override fun getLong(key: String): Long = Conversions.toLong(map[key].toString())
    override fun getFloat(key: String): Float = Conversions.toFloat(map[key].toString())
    override fun getDouble(key: String): Double = Conversions.toDouble(map[key].toString())
    override fun getInstant(key: String): Instant = Conversions.toInstant(map[key].toString())
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(map[key].toString())
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(map[key].toString())
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(map[key].toString())
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(map[key].toString())
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(map[key].toString())
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = Conversions.toZonedDateTimeUtc(map[key].toString())

    override fun get(key: String): Any? = if (map.contains(key)) map[key] else null
    //override fun getObject(key: String): Any? = if (_map.contains(key)) _map[key] else null
    override fun containsKey(key: String): Boolean = map.contains(key)
    override fun size(): Int = map.size

    override fun add(key: String, value: Any): Inputs {
        val newMap = map.plus(key to value)
        return InputArgs(newMap, decryptor)
    }
}
