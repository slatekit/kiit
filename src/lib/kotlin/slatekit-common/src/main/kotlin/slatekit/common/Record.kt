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

import slatekit.common.ids.UniqueId
//import java.time.*
import org.threeten.bp.*

interface Record : Inputs {

    fun getPos(name:String):Int
    fun getName(pos:Int):String
    fun contains(name:String):Boolean

    fun getString(pos: Int): String = getString(getName(pos))
    fun getBool(pos: Int): Boolean = getBool(getName(pos))
    fun getShort(pos: Int): Short = getShort(getName(pos))
    fun getInt(pos: Int): Int = getInt(getName(pos))
    fun getLong(pos: Int): Long = getLong(getName(pos))
    fun getFloat(pos: Int): Float = getFloat(getName(pos))
    fun getDouble(pos: Int): Double = getDouble(getName(pos))
    fun getInstant(pos: Int): Instant = getInstant(getName(pos))
    fun getDateTime(pos: Int): DateTime = getDateTime(getName(pos))
    fun getLocalDate(pos: Int): LocalDate = getLocalDate(getName(pos))
    fun getLocalTime(pos: Int): LocalTime = getLocalTime(getName(pos))
    fun getLocalDateTime(pos: Int): LocalDateTime = getLocalDateTime(getName(pos))
    fun getZonedDateTime(pos: Int): ZonedDateTime = getZonedDateTime(getName(pos))
    fun getZonedDateTimeUtc(pos: Int): ZonedDateTime = getZonedDateTimeUtc(getName(pos))
    fun getUUID(pos: Int): java.util.UUID = getUUID(getName(pos))
    fun getUniqueId(pos: Int): UniqueId = getUniqueId(getName(pos))
}
