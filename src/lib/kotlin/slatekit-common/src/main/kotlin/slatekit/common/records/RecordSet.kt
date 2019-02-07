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
import slatekit.common.ids.UniqueId
import java.sql.ResultSet
import java.time.*
import java.util.UUID

class RecordSet(private val rs: ResultSet) : Record {

    override val raw: Any = rs
    override fun size(): Int = 0
    override fun get(key: String): Any? = rs.getString(key)
    override fun getPos(name:String):Int = rs.findColumn(name)
    override fun getName(pos:Int):String = rs.metaData.getColumnName(pos)
    override fun contains(name:String):Boolean = rs.findColumn(name) > -1
    override fun containsKey(name:String):Boolean = rs.findColumn(name) > -1

    override fun getString(key: String): String = rs.getString(key)
    override fun getBool(key: String): Boolean = rs.getBoolean(key)

    override fun getShort(key: String): Short = rs.getShort(key)
    override fun getInt(key: String): Int = rs.getInt(key)
    override fun getLong(key: String): Long = rs.getLong(key)
    override fun getFloat(key: String): Float = rs.getFloat(key)
    override fun getDouble(key: String): Double = rs.getDouble(key)
    override fun getLocalDate(key: String): LocalDate = rs.getDate(key).toLocalDate()
    override fun getLocalTime(key: String): LocalTime = rs.getTime(key).toLocalTime()
    override fun getLocalDateTime(key: String): LocalDateTime = rs.getTimestamp(key).toLocalDateTime()
    override fun getZonedDateTime(key: String): ZonedDateTime = rs.getTimestamp(key).toLocalDateTime().atZone(ZoneId.systemDefault())
    override fun getInstant(key: String): Instant = rs.getTimestamp(key).toInstant()
    override fun getDateTime(key: String): DateTime = rs.getTimestamp(key).let { DateTime.of(it) }

    override fun getDateTimeAsUTC(pos: Int): DateTime {
        val ts = rs.getTimestamp(pos)
        return ts.let { DateTime.of(ts).atUtcLocal() }
    }

    override fun getDateTimeAsUTC(name: String): DateTime {
        val ts = rs.getTimestamp(name)
        return ts.let { DateTime.of(ts).atUtcLocal() }
    }


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
