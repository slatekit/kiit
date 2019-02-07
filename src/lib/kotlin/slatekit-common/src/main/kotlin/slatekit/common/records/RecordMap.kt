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

package slatekit.common.records

import slatekit.common.DateTime
import slatekit.common.utils.ListMap
import slatekit.common.ids.UniqueId
import java.time.*
import java.util.*

class RecordMap(private val rs: ListMap<String, Any>) : Record {

    override val raw: Any = rs
    override fun size(): Int = rs.size
    override fun get(key: String): Any? = rs.get(key)
    override fun getPos(key:String):Int = rs.keys().indexOf(key)
    override fun getName(pos:Int):String = rs.keys()[pos]
    override fun contains(key:String):Boolean = rs.contains(key)
    override fun containsKey(key: String): Boolean = rs.contains(key)

    override fun getString(key: String): String = rs.get(key) as String
    override fun getBool(key: String): Boolean = rs.get(key) as Boolean
    override fun getShort(key: String): Short = rs.get(key) as Short
    override fun getInt(key: String): Int = rs.get(key) as Int
    override fun getLong(key: String): Long = rs.get(key) as Long
    override fun getFloat(key: String): Float = rs.get(key) as Float
    override fun getDouble(key: String): Double = rs.get(key) as Double
    override fun getUUID(key: String): java.util.UUID = rs.get(key) as UUID
    override fun getUniqueId(key: String): UniqueId = rs.get(key) as UniqueId
    override fun getLocalDate(key: String): LocalDate = (rs.get(key) as java.sql.Date).toLocalDate()
    override fun getLocalTime(key: String): LocalTime = (rs.get(key) as java.sql.Time).toLocalTime()
    override fun getLocalDateTime(key: String): LocalDateTime = (rs.get(key) as java.sql.Timestamp).toLocalDateTime()
    override fun getZonedDateTime(key: String): ZonedDateTime = (rs.get(key) as java.sql.Timestamp).toLocalDateTime().atZone(ZoneId.systemDefault())
    override fun getInstant(key: String): Instant = (rs.get(key) as java.sql.Timestamp).toInstant()
    override fun getDateTime(key: String): DateTime = (rs.get(key) as java.sql.Timestamp).let { DateTime.of(it) }
    override fun getDateTimeAsUTC(key: String): DateTime = (rs.get(key) as java.sql.Timestamp).let { DateTime.of(it, DateTime.UTC) }
}
