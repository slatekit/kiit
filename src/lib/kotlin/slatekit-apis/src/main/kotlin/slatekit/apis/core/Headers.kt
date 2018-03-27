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

package slatekit.apis.core

import slatekit.common.Conversions
import slatekit.common.DateTime
import slatekit.common.InputFuncs
import slatekit.common.Inputs
import slatekit.common.encrypt.Encryptor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime


data class Headers(val headers: Map<String, String>,
                   val enc: Encryptor?) : slatekit.common.Meta {

    override val raw:Any = headers
    override fun get(key: String): Any? = getInternal(key)
    override fun getObject(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = headers.contains(key)
    override fun size(): Int = headers.size


    override fun getString(key: String): String = InputFuncs.decrypt(getInternalString(key).trim(), { it -> enc?.decrypt(it) ?: it })
    override fun getBool(key: String): Boolean = Conversions.toBool(getStringRaw(key))
    override fun getShort(key: String): Short = Conversions.toShort(getStringRaw(key))
    override fun getInt(key: String): Int = Conversions.toInt(getStringRaw(key))
    override fun getLong(key: String): Long = Conversions.toLong(getStringRaw(key))
    override fun getFloat(key: String): Float = Conversions.toFloat(getStringRaw(key))
    override fun getDouble(key: String): Double = Conversions.toDouble(getStringRaw(key))
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(getStringRaw(key))
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(getStringRaw(key))
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(getStringRaw(key))
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(getStringRaw(key))
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(getStringRaw(key))


    fun getInternal(key: String): Any? {
        return if (containsKey(key)) {
            val value = headers[key]
            if (value != null && value is String) {
                value.trim()
            }
            else {
                value
            }
        }
        else {
            null
        }
    }


    fun getInternalString(key: String): String {
        return if (containsKey(key)) {
            val value = headers[key]
            if (value != null && value is String) {
                value.trim()
            }
            else {
                value ?: ""
            }
        }
        else {
            ""
        }
    }


    fun getStringRaw(key: String): String = getInternalString(key).trim()
}