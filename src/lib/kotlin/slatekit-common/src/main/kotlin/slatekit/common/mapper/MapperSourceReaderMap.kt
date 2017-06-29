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
import slatekit.common.ListMap


class MapperSourceReaderMap(val rs: ListMap<String, Any>) : MappedSourceReader {

    override fun init(rec: List<String>): Unit {
    }


    override fun getString(pos: Int): String = rs.getAt(pos) as String
    override fun getString(name: String): String = rs.get(name) as String


    override fun getShort(pos: Int): Short = rs.getAt(pos) as Short
    override fun getShort(name: String): Short = rs.get(name) as Short


    override fun getInt(pos: Int): Int = rs.getAt(pos) as Int
    override fun getInt(name: String): Int = rs.get(name) as Int


    override fun getFloat(pos: Int): Float = rs.getAt(pos) as Float
    override fun getFloat(name: String): Float = rs.get(name) as Float


    override fun getDouble(pos: Int): Double = rs.getAt(pos) as Double
    override fun getDouble(name: String): Double = rs.get(name) as Double


    override fun getLong(pos: Int): Long = rs.getAt(pos) as Long
    override fun getLong(name: String): Long = rs.get(name) as Long


    override fun getBool(pos: Int): Boolean = rs.getAt(pos) as Boolean
    override fun getBool(name: String): Boolean = rs.get(name) as Boolean


    override fun getVersion(): String = ""


    override fun getDate(pos: Int): DateTime = DateTime(rs.getAt(pos) as java.sql.Timestamp)
    override fun getDate(name: String): DateTime = DateTime(rs.get(name) as java.sql.Timestamp)


    override fun getOrDefault(pos: Int, defaultVal: String): String = rs.getAt(pos) as String
    override fun getOrDefault(name: String, defaultVal: String): String = rs.get(name) as String


    override fun getBoolOrDefault(pos: Int, defaultVal: Boolean): Boolean = rs.getAt(pos) as Boolean
    override fun getBoolOrDefault(name: String, defaultVal: Boolean): Boolean = rs.get(name) as Boolean
}
