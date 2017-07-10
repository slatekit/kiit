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

/**
 * Created by kishorereddy on 5/25/17.
 */
class InputArgs(val _map: Map<String, Any>,
                private val _decryptor: ((String) -> String)? = null) : Inputs {

    override val raw:Any =_map

    override fun getString(key: String): String = InputFuncs.decrypt(_map[key].toString(), _decryptor)
    override fun getBool(key: String): Boolean = Conversions.toBool(_map[key].toString())
    override fun getShort(key: String): Short = Conversions.toShort(_map[key].toString())
    override fun getInt(key: String): Int = Conversions.toInt(_map[key].toString())
    override fun getLong(key: String): Long = Conversions.toLong(_map[key].toString())
    override fun getFloat(key: String): Float = Conversions.toFloat(_map[key].toString())
    override fun getDouble(key: String): Double = Conversions.toDouble(_map[key].toString())
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(_map[key].toString())
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(_map[key].toString())
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(_map[key].toString())
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(_map[key].toString())
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(_map[key].toString())


    override fun get(key: String): Any? = if (_map.contains(key)) _map[key] else null
    override fun getObject(key: String): Any? = if (_map.contains(key)) _map[key] else null
    override fun containsKey(key: String): Boolean = _map.contains(key)
    override fun size(): Int = _map.size
}
