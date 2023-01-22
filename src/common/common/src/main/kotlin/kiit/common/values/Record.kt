/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 *
 *  </kiit_header>
 */

package kiit.common.values

import kiit.common.ids.UPID
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import kiit.common.DateTime

/**
 * Supplies default methods for reading inputs from a record
 * based data structure.
 */
interface Record : Inputs {

    fun getPos(name: String): Int
    fun getName(pos: Int): String
    fun contains(name: String): Boolean
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
    fun getUPID(pos: Int): UPID = getUPID(getName(pos))
}
