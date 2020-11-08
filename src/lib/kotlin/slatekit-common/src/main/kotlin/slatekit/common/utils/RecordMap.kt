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

package slatekit.common.utils

import slatekit.common.DateTime
import slatekit.common.Record
import slatekit.common.ids.UPID
//import java.time.*
import org.threeten.bp.*
import slatekit.common.DateTimes
import slatekit.common.ext.atZone
import slatekit.common.ext.date
import slatekit.common.ext.local
import slatekit.common.ext.time
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
    override fun getUPID(key: String): UPID = rs.get(key) as UPID
    override fun getInstant(key: String): Instant = DateTimes.of(rs.get(key) as java.sql.Timestamp).toInstant()
    override fun getDateTime(key: String): DateTime = (rs.get(key) as java.sql.Timestamp).let { DateTimes.of(it) }
    override fun getLocalDate(key: String): LocalDate = DateTimes.of(rs.get(key) as java.sql.Date).date()
    override fun getLocalTime(key: String): LocalTime = DateTimes.of(rs.get(key) as java.sql.Time).time()
    override fun getLocalDateTime(key: String): LocalDateTime = DateTimes.of(rs.get(key) as java.sql.Timestamp).local()
    override fun getZonedDateTime(key: String): ZonedDateTime = DateTimes.of(rs.get(key) as java.sql.Timestamp).atZone(ZoneId.systemDefault())
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = DateTimes.build((rs.get(key) as java.sql.Timestamp), DateTimes.UTC)
}
