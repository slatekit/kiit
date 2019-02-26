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

package slatekit.db

import slatekit.common.DateTime
import slatekit.common.Record
import java.sql.ResultSet
//import java.time.*
import org.threeten.bp.*
import slatekit.common.DateTimes
import slatekit.common.ext.atZone
import slatekit.common.ext.date
import slatekit.common.ext.local
import slatekit.common.ext.time

class RecordSet(private val rs: ResultSet) : Record {

    override val raw: Any = rs
    override fun size(): Int = 0
    override fun get(key: String): Any? = rs.getString(key)
    override fun getPos(name:String):Int = rs.findColumn(name)
    override fun getName(pos:Int):String = rs.metaData.getColumnName(pos)
    override fun contains(name:String):Boolean = rs.findColumn(name) > -1
    override fun containsKey(key:String):Boolean = rs.findColumn(key) > -1

    override fun getString(key: String): String = rs.getString(key)
    override fun getBool(key: String): Boolean = rs.getBoolean(key)

    override fun getShort(key: String): Short = rs.getShort(key)
    override fun getInt(key: String): Int = rs.getInt(key)
    override fun getLong(key: String): Long = rs.getLong(key)
    override fun getFloat(key: String): Float = rs.getFloat(key)
    override fun getDouble(key: String): Double = rs.getDouble(key)
    override fun getInstant(key: String): Instant = DateTimes.of(rs.getTimestamp(key)).toInstant()
    override fun getDateTime(key: String): DateTime = rs.getTimestamp(key).let { DateTimes.of(it) }
    override fun getLocalDate(key: String): LocalDate = DateTimes.of(rs.getDate(key)).date()
    override fun getLocalTime(key: String): LocalTime = DateTimes.of(rs.getTime(key)).time()
    override fun getLocalDateTime(key: String): LocalDateTime = DateTimes.of(rs.getTimestamp(key)).local()
    override fun getZonedDateTime(key: String): ZonedDateTime = DateTimes.of(rs.getTimestamp(key)).atZone(ZoneId.systemDefault())
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = DateTimes.build(rs.getTimestamp(key), DateTimes.UTC)

    // Helpers
    override fun <T> getOrNull(key: String, fetcher: (String) -> T): T? {
        return if (!containsKey(key))
            null
        else if (rs.getObject(key) == null)
            null
        else
            fetcher(key)
    }

    override fun <T> getOrElse(key: String, fetcher: (String) -> T, default: T): T {
        return if (!containsKey(key))
            default
        else if (rs.getObject(key) == null)
            default
        else
            fetcher(key)
    }
}
