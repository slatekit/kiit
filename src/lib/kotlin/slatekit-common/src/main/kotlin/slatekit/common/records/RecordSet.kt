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
import slatekit.common.UniqueId
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import java.nio.ByteBuffer


class RecordSet(val rs: ResultSet) : Record {


    override fun init(rec: List<String>): Unit {}


    override fun getString(pos: Int): String = rs.getString(pos)
    override fun getString(name: String): String = rs.getString(name)


    override fun getBool(pos: Int): Boolean = rs.getBoolean(pos)
    override fun getBool(name: String): Boolean = rs.getBoolean(name)


    override fun getShort(pos: Int): Short = rs.getShort(pos)
    override fun getShort(name: String): Short = rs.getShort(name)


    override fun getInt(pos: Int): Int = rs.getInt(pos)
    override fun getInt(name: String): Int = rs.getInt(name)


    override fun getLong(pos: Int): Long = rs.getLong(pos)
    override fun getLong(name: String): Long = rs.getLong(name)


    override fun getFloat(pos: Int): Float = rs.getFloat(pos)
    override fun getFloat(name: String): Float = rs.getFloat(name)


    override fun getDouble(pos: Int): Double = rs.getDouble(pos)
    override fun getDouble(name: String): Double = rs.getDouble(name)


    override fun getLocalDate(pos: Int): LocalDate = rs.getDate(pos).toLocalDate()
    override fun getLocalDate(name: String): LocalDate = rs.getDate(name).toLocalDate()


    override fun getLocalTime(pos: Int): LocalTime = rs.getTime(pos).toLocalTime()
    override fun getLocalTime(name: String): LocalTime = rs.getTime(name).toLocalTime()


    override fun getLocalDateTime(pos: Int): LocalDateTime =  rs.getTimestamp(pos).toLocalDateTime()
    override fun getLocalDateTime(name: String): LocalDateTime = rs.getTimestamp(name).toLocalDateTime()


    override fun getInstant(pos: Int): Instant =  rs.getTimestamp(pos).toInstant()
    override fun getInstant(name: String): Instant = rs.getTimestamp(name).toInstant()


    override fun getDateTime(pos: Int): DateTime = DateTime.of(rs.getTimestamp(pos))
    override fun getDateTime(name: String): DateTime = DateTime.of(rs.getTimestamp(name))


    override fun getDateTimeAsUTC(pos:Int):DateTime = DateTime.of(rs.getTimestamp(pos) as java.sql.Timestamp).atUtcLocal()
    override fun getDateTimeAsUTC(name:String):DateTime = DateTime.of(rs.getTimestamp(name) as java.sql.Timestamp).atUtcLocal()


    override fun getUUID(pos:Int): java.util.UUID     = UUID.fromString(rs.getString(pos))
    override fun getUUID(name:String): java.util.UUID = UUID.fromString(rs.getString(name))


    override fun getUniqueId(pos:Int): UniqueId     = UniqueId.fromString(rs.getString(pos))
    override fun getUniqueId(name:String): UniqueId = UniqueId.fromString(rs.getString(name))


    //private fun getUUIDFromBytes(bytes:ByteArray): UUID {
    //    val buffer = ByteBuffer.wrap(bytes)
    //    val msb = buffer.long
    //    val lsb = buffer.long
    //    return UUID(msb, lsb)
    //}
}
