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
import slatekit.common.ListMap
import slatekit.common.UniqueId
import java.time.*
import java.util.*


class RecordMap(val rs: ListMap<String, Any>) : Record {

    override fun init(rec: List<String>): Unit {
    }


    override fun getString(pos: Int): String = rs.getAt(pos) as String
    override fun getString(name: String): String = rs.get(name) as String


    override fun getBool(pos: Int): Boolean = rs.getAt(pos) as Boolean
    override fun getBool(name: String): Boolean = rs.get(name) as Boolean


    override fun getShort(pos: Int): Short = rs.getAt(pos) as Short
    override fun getShort(name: String): Short = rs.get(name) as Short


    override fun getInt(pos: Int): Int = rs.getAt(pos) as Int
    override fun getInt(name: String): Int = rs.get(name) as Int


    override fun getLong(pos: Int): Long = rs.getAt(pos) as Long
    override fun getLong(name: String): Long = rs.get(name) as Long


    override fun getFloat(pos: Int): Float = rs.getAt(pos) as Float
    override fun getFloat(name: String): Float = rs.get(name) as Float


    override fun getDouble(pos: Int): Double = rs.getAt(pos) as Double
    override fun getDouble(name: String): Double = rs.get(name) as Double


    override fun getLocalDate(pos: Int): LocalDate = ( rs.getAt(pos) as java.sql.Date ).toLocalDate()
    override fun getLocalDate(name: String): LocalDate = ( rs.get(name) as java.sql.Date ).toLocalDate()


    override fun getLocalTime(pos: Int): LocalTime = ( rs.getAt(pos) as java.sql.Time ).toLocalTime()
    override fun getLocalTime(name: String): LocalTime = ( rs.get(name) as java.sql.Time).toLocalTime()


    override fun getLocalDateTime(pos: Int): LocalDateTime = ( rs.getAt(pos) as java.sql.Timestamp ).toLocalDateTime()
    override fun getLocalDateTime(name: String): LocalDateTime = ( rs.get(name) as java.sql.Timestamp ).toLocalDateTime()


    override fun getInstant(pos: Int): Instant = ( rs.getAt(pos) as java.sql.Timestamp ).toInstant()
    override fun getInstant(name: String): Instant = ( rs.get(name) as java.sql.Timestamp ).toInstant()


    override fun getDateTime(pos: Int): DateTime = DateTime.of(rs.getAt(pos) as java.sql.Timestamp)
    override fun getDateTime(name: String): DateTime = DateTime.of(rs.get(name) as java.sql.Timestamp)


    override fun getDateTimeAsUTC(pos:Int):DateTime = DateTime.of(rs.getAt(pos) as java.sql.Timestamp, DateTime.UTC)
    override fun getDateTimeAsUTC(name:String):DateTime = DateTime.of(rs.get(name) as java.sql.Timestamp, DateTime.UTC)


    override fun getUUID(pos:Int): java.util.UUID     = rs.getAt(pos) as UUID
    override fun getUUID(name:String): java.util.UUID = rs.get(name) as UUID


    override fun getUniqueId(pos:Int): UniqueId     = rs.getAt(pos) as UniqueId
    override fun getUniqueId(name:String): UniqueId = rs.get(name) as UniqueId
}
