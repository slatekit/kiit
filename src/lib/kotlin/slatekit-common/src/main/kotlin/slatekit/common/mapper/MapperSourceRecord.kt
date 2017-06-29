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
import java.sql.ResultSet


class MapperSourceRecord(val rs: ResultSet) : MappedSourceReader {


    override fun init(rec: List<String>): Unit {}


    override fun getString(pos: Int): String = rs.getString(pos)
    override fun getString(name: String): String = rs.getString(name)


    override fun getShort(pos: Int): Short = rs.getShort(pos)
    override fun getShort(name: String): Short = rs.getShort(name)


    override fun getInt(pos: Int): Int = rs.getInt(pos)
    override fun getInt(name: String): Int = rs.getInt(name)


    override fun getFloat(pos: Int): Float = rs.getFloat(pos)
    override fun getFloat(name: String): Float = rs.getFloat(name)


    override fun getDouble(pos: Int): Double = rs.getDouble(pos)
    override fun getDouble(name: String): Double = rs.getDouble(name)


    override fun getLong(pos: Int): Long = rs.getLong(pos)
    override fun getLong(name: String): Long = rs.getLong(name)


    override fun getBool(pos: Int): Boolean = rs.getBoolean(pos)
    override fun getBool(name: String): Boolean = rs.getBoolean(name)


    override fun getVersion(): String = ""


    override fun getDate(pos: Int): DateTime = DateTime(rs.getTimestamp(pos))
    override fun getDate(name: String): DateTime = DateTime(rs.getTimestamp(name))


    override fun getOrDefault(pos: Int, defaultVal: String): String = rs.getString(pos)
    override fun getOrDefault(name: String, defaultVal: String): String = rs.getString(name)


    override fun getBoolOrDefault(pos: Int, defaultVal: Boolean): Boolean = rs.getBoolean(pos)
    override fun getBoolOrDefault(name: String, defaultVal: Boolean): Boolean = rs.getBoolean(name)
}
