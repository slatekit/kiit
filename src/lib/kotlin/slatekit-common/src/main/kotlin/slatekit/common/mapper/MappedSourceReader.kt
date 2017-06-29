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

package slatekit.common.mapper


import slatekit.common.DateTime


interface MappedSourceReader {
    fun init(rec: List<String>): Unit

    fun getVersion(): String

    fun getString(pos: Int): String
    fun getString(name: String): String

    fun getShort(pos: Int): Short
    fun getShort(name: String): Short

    fun getInt(pos: Int): Int
    fun getInt(name: String): Int

    fun getLong(pos: Int): Long
    fun getLong(name: String): Long

    fun getFloat(pos: Int): Float
    fun getFloat(name: String): Float

    fun getDouble(pos: Int): Double
    fun getDouble(name: String): Double

    fun getBool(pos: Int): Boolean
    fun getBool(name: String): Boolean

    fun getDate(pos: Int): DateTime
    fun getDate(name: String): DateTime

    fun getOrDefault(pos: Int, defaultVal: String): String
    fun getOrDefault(name: String, defaultVal: String): String

    fun getBoolOrDefault(pos: Int, defaultVal: Boolean): Boolean
    fun getBoolOrDefault(name: String, defaultVal: Boolean): Boolean
}
