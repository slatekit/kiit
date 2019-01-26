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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class RecordSet(val rs: ResultSet) : Record {

    override fun getPos(name:String):Int = rs.findColumn(name)
    override fun getName(pos:Int):String = rs.metaData.getColumnName(pos)
    override fun contains(name:String):Boolean = rs.findColumn(name) > -1

    override fun getString(pos: Int): String? = rs.getString(pos)
    override fun getString(name: String): String? = rs.getString(name)

    override fun getBool(pos: Int): Boolean? = rs.getBoolean(pos)
    override fun getBool(name: String): Boolean? = rs.getBoolean(name)

    override fun getShort(pos: Int): Short? = rs.getShort(pos)
    override fun getShort(name: String): Short? = rs.getShort(name)

    override fun getInt(pos: Int): Int? = rs.getInt(pos)
    override fun getInt(name: String): Int? = rs.getInt(name)

    override fun getLong(pos: Int): Long? = rs.getLong(pos)
    override fun getLong(name: String): Long? = rs.getLong(name)

    override fun getFloat(pos: Int): Float? = rs.getFloat(pos)
    override fun getFloat(name: String): Float? = rs.getFloat(name)

    override fun getDouble(pos: Int): Double? = rs.getDouble(pos)
    override fun getDouble(name: String): Double? = rs.getDouble(name)

    override fun getUUID(pos: Int): java.util.UUID? = rs.getString(pos)?.let { UUID.fromString(it) }
    override fun getUUID(name: String): java.util.UUID? = rs.getString(name)?.let { UUID.fromString(it) }

    override fun getUniqueId(pos: Int): UniqueId? = rs.getString(pos)?.let { UniqueId.fromString(it) }
    override fun getUniqueId(name: String): UniqueId? = rs.getString(name)?.let { UniqueId.fromString(it) }

    override fun getLocalDate(pos: Int): LocalDate? = rs.getDate(pos)?.toLocalDate()
    override fun getLocalDate(name: String): LocalDate? = rs.getDate(name)?.toLocalDate()

    override fun getLocalTime(pos: Int): LocalTime? = rs.getTime(pos)?.toLocalTime()
    override fun getLocalTime(name: String): LocalTime? = rs.getTime(name)?.toLocalTime()

    override fun getLocalDateTime(pos: Int): LocalDateTime? = rs.getTimestamp(pos)?.toLocalDateTime()
    override fun getLocalDateTime(name: String): LocalDateTime? = rs.getTimestamp(name)?.toLocalDateTime()

    override fun getInstant(pos: Int): Instant? = rs.getTimestamp(pos)?.toInstant()
    override fun getInstant(name: String): Instant? = rs.getTimestamp(name)?.toInstant()

    override fun getDateTime(pos: Int): DateTime? = rs.getTimestamp(pos)?.let { DateTime.of(it) }
    override fun getDateTime(name: String): DateTime? = rs.getTimestamp(name)?.let { DateTime.of(it) }

    override fun getDateTimeAsUTC(pos: Int): DateTime? {
        val ts = rs.getTimestamp(pos)
        return ts?.let { DateTime.of(ts).atUtcLocal() }
    }

    override fun getDateTimeAsUTC(name: String): DateTime? {
        val ts = rs.getTimestamp(name)
        return ts?.let { DateTime.of(ts).atUtcLocal() }
    }

    // private fun getUUIDFromBytes(bytes:ByteArray): UUID {
    //    val buffer = ByteBuffer.wrap(bytes)
    //    val msb = buffer.long
    //    val lsb = buffer.long
    //    return UUID(msb, lsb)
    // }
}
