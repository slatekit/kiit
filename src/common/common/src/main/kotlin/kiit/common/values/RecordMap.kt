/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.common.values

import kiit.common.DateTime
import kiit.common.ids.UPID
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import kiit.common.DateTimes
import kiit.common.ext.atZone
import kiit.common.ext.date
import kiit.common.ext.local
import kiit.common.ext.time
import java.util.UUID

class RecordMap(private val rs: ListMap<String, Any?>) : Record {

    override val raw: Any = rs
    override fun size(): Int = rs.size
    override fun get(key: String): Any? = rs.get(key)
    override fun getPos(name: String): Int = rs.keys().indexOf(name)
    override fun getName(pos: Int): String = rs.keys()[pos]
    override fun contains(name: String): Boolean = rs.contains(name)
    override fun containsKey(key: String): Boolean = rs.contains(key)
    override fun getString(key: String): String = rs.get(key) as String
    override fun getBool(key: String): Boolean = rs.get(key) as Boolean
    override fun getShort(key: String): Short = rs.get(key) as Short
    override fun getInt(key: String): Int = rs.get(key) as Int
    override fun getLong(key: String): Long = rs.get(key) as Long
    override fun getFloat(key: String): Float = rs.get(key) as Float
    override fun getDouble(key: String): Double = rs.get(key) as Double
    override fun getUUID(key: String): UUID = rs.get(key) as UUID
    override fun getUPID(key: String): UPID = rs.get(key) as UPID
    override fun getInstant(key: String): Instant = DateTimes.of(rs.get(key) as java.sql.Timestamp).toInstant()
    override fun getDateTime(key: String): DateTime = (rs.get(key) as java.sql.Timestamp).let { DateTimes.of(it) }
    override fun getLocalDate(key: String): LocalDate = DateTimes.of(rs.get(key) as java.sql.Date).date()
    override fun getLocalTime(key: String): LocalTime = DateTimes.of(rs.get(key) as java.sql.Time).time()
    override fun getLocalDateTime(key: String): LocalDateTime = DateTimes.of(rs.get(key) as java.sql.Timestamp).local()
    override fun getZonedDateTime(key: String): ZonedDateTime =
        DateTimes.of(rs.get(key) as java.sql.Timestamp).atZone(ZoneId.systemDefault())

    override fun getZonedDateTimeUtc(key: String): ZonedDateTime =
        DateTimes.build((rs.get(key) as java.sql.Timestamp), DateTimes.UTC)
}
