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

package slatekit.server.spark

import slatekit.common.DateTime
import slatekit.common.InputFuncs
import slatekit.common.Inputs
import slatekit.common.encrypt.Encryptor
import spark.Request


data class HttpHeaders(val req: Request, val enc: Encryptor?) : Inputs {

    override fun get(key: String): Any? = getInternal(key)
    override fun getObject(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = req.headers().contains(key)
    override fun size(): Int = req.headers().size


    override fun getString(key: String): String = InputFuncs.decrypt(getInternalString(key).trim(), { it -> enc?.decrypt(it) ?: it })
    override fun getDate(key: String): DateTime = InputFuncs.convertDate(getInternalString(key).trim())
    override fun getBool(key: String): Boolean = getInternalString(key).trim().toBoolean()
    override fun getShort(key: String): Short = getInternalString(key).trim().toShort()
    override fun getInt(key: String): Int = getInternalString(key).trim().toInt()
    override fun getLong(key: String): Long = getInternalString(key).trim().toLong()
    override fun getDouble(key: String): Double = getInternalString(key).trim().toDouble()
    override fun getFloat(key: String): Float = getInternalString(key).trim().toFloat()


    fun getInternal(key: String): Any? {
        return if (containsKey(key)) {
            val value = req.headers(key)
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
            val value = req.headers(key)
            if (value != null && value is String) {
                value.trim()
            }
            else {
                value
            }
        }
        else {
            ""
        }
    }
}