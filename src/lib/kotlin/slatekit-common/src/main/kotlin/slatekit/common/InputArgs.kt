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

/**
 * Created by kishorereddy on 5/25/17.
 */
class InputArgs(val _map: Map<String, Any>,
                private val _decryptor: ((String) -> String)? = null) : Inputs {

    override fun getString(key: String): String = InputFuncs.decrypt(_map[key].toString(), _decryptor)
    override fun getBool(key: String): Boolean = _map[key].toString().toBoolean()
    override fun getShort(key: String): Short = _map[key].toString().toShort()
    override fun getInt(key: String): Int = _map[key].toString().toInt()
    override fun getLong(key: String): Long = _map[key].toString().toLong()
    override fun getFloat(key: String): Float = _map[key].toString().toFloat()
    override fun getDouble(key: String): Double = _map[key].toString().toDouble()


    override fun getDate(key: String): DateTime {

        return when (_map[key]) {
            DateTime -> _map[key] as (DateTime)
            Long     -> DateTime.parseNumericDate12(_map[key].toString())
            else     -> InputFuncs.convertDate(_map[key].toString())
        }
    }


    override fun get(key: String): Any? = if (_map.contains(key)) _map[key] else null
    override fun getObject(key: String): Any? = if (_map.contains(key)) _map[key] else null
    override fun containsKey(key: String): Boolean = _map.contains(key)
    override fun size(): Int = _map.size
}
