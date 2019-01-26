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
import java.time.*

interface Record {

    fun getPos(name:String):Int
    fun getName(pos:Int):String
    fun contains(name:String):Boolean

    fun getString(pos: Int): String?
    fun getString(name: String): String?

    fun getOrDefault(pos:Int, value:String):String = getString(pos) ?: value
    fun getOrDefault(name:String, value:String):String = getString(name) ?: value

    fun getBool(pos: Int): Boolean?
    fun getBool(name: String): Boolean?

    fun getShort(pos: Int): Short?
    fun getShort(name: String): Short?

    fun getInt(pos: Int): Int?
    fun getInt(name: String): Int?

    fun getLong(pos: Int): Long?
    fun getLong(name: String): Long?

    fun getFloat(pos: Int): Float?
    fun getFloat(name: String): Float?

    fun getDouble(pos: Int): Double?
    fun getDouble(name: String): Double?

    fun getUUID(pos: Int): java.util.UUID?
    fun getUUID(name: String): java.util.UUID?

    fun getUniqueId(pos: Int): UniqueId?
    fun getUniqueId(name: String): UniqueId?

    // Assumes DateTime at local zone
    fun getDateTime(pos: Int): DateTime?
    fun getDateTime(name: String): DateTime?

    // Assumes DateTime as UTC
    fun getDateTimeAsUTC(pos: Int): DateTime?
    fun getDateTimeAsUTC(name: String): DateTime?

    fun getLocalDate(pos: Int): LocalDate?
    fun getLocalDate(name: String): LocalDate?

    fun getLocalTime(pos: Int): LocalTime?
    fun getLocalTime(name: String): LocalTime?

    fun getLocalDateTime(pos: Int): LocalDateTime?
    fun getLocalDateTime(name: String): LocalDateTime?

    fun getZonedDateTime(pos: Int): ZonedDateTime? = getDateTime(pos)?.raw
    fun getZonedDateTime(name: String): ZonedDateTime? = getDateTime(name)?.raw

    fun getInstant(pos: Int): Instant?
    fun getInstant(name: String): Instant?

    // ========================================================================
    // All the methods below get the datetime from the underlying value
    // which is assumed to be UTC. So we load underlying as UTC and
    // convert it to accordingly to the local zone
    fun getLocalDateTimeFromUTC(pos: Int): LocalDateTime? {
        val atUtc = getDateTimeAsUTC(pos)
        val local = atUtc?.atZone(ZoneId.systemDefault())
        return local?.local()
    }

    fun getLocalDateTimeFromUTC(name: String): LocalDateTime? {
        val atUtc = getDateTimeAsUTC(name)
        val local = atUtc?.atZone(ZoneId.systemDefault())
        return local?.local()
    }

    fun getZonedDateTimeLocalFromUTC(pos: Int): ZonedDateTime? = getDateTimeAsUTC(pos)?.atZone(ZoneId.systemDefault())?.raw
    fun getZonedDateTimeLocalFromUTC(name: String): ZonedDateTime? = getDateTimeAsUTC(name)?.atZone(ZoneId.systemDefault())?.raw

    fun getDateTimeLocalFromUTC(pos: Int): DateTime? = getDateTimeAsUTC(pos)?.atZone(ZoneId.systemDefault())
    fun getDateTimeLocalFromUTC(name: String): DateTime? = getDateTimeAsUTC(name)?.atZone(ZoneId.systemDefault())
}
