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

package slatekit.common.conf


import slatekit.common.DateTime
import slatekit.common.InputFuncs
import slatekit.common.encrypt.Encryptor
import java.util.*

/**
 *  Conf is a wrapper around the typesafe config with additional support for:
 *
1. RESOURCES
Use a prefix for indicating where to load config from.
- "jars://"  for the resources directory in the jar.
- "user://" for the user.home directory.
- "file://" for an explicit path to the file
- "file://" for relative path to the file from working directory

Examples:
- jar://env.qa.conf
- user://${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
- file://c:/slatekit/${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
- file://./conf/env.qa.conf


2. LOADING
Use a short-hand syntax for specifying the files to merge and fallback with
Examples:
- primary
- primary, fallback1
- primary, fallback1, fallback2


3. FUNCTIONS:
Use functions in the config to resolve values dynamically.
You can use the resolveString method to resolve the value dynamically.
Note: This uses the slatekit.common.subs component.
Examples: ( props inside your .conf file )
- tag      : "@{today.yyyymmdd-hhmmss}"


4. DEFAULTS:
Use optional default values for getting strings, int, doubles, bools.
Examples:
- getStringOrElse
- getIntOrElse
- getBoolOrElse
- getDoubleOrElse
- getDateOrElse


5. MAPPING
Map slatekit objects automatically from the conf settings. Built in mappers for the
Examples:
- "env"   : Environment selection ( dev, qa, stg, prod ) etc.
- "login" : slatekit.common.Credentials object
- "db"    : database connection strings
- "api"   : standardized api keys
 *

 * @param fileName
 * @param enc
 * @param config
 */
class Config(fileName: String? = null,
             enc: Encryptor? = null,
             config: Properties? = null
)
    : ConfigBase({ raw -> enc?.decrypt(raw) ?: raw }) {

    private val _fileName = fileName
    private val _enc = enc

    /**
     * Get or load the config object
     */
    private val _config: Properties = config ?: ConfFuncs.loadPropertiesFrom(fileName)


    override fun get(key: String): Any? = getInternal(key)
    override fun getObject(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = _config.containsKey(key)
    override fun size(): Int = _config.values.size


    override fun getString(key: String): String = InputFuncs.decrypt(_config.getProperty(key).trim(), _encryptor)
    override fun getDate(key: String): DateTime = InputFuncs.convertDate(_config.getProperty(key).trim())
    override fun getBool(key: String): Boolean = _config.getProperty(key)!!.trim().toBoolean()
    override fun getShort(key: String): Short = _config.getProperty(key)!!.trim().toShort()
    override fun getInt(key: String): Int = _config.getProperty(key)!!.trim().toInt()
    override fun getLong(key: String): Long = _config.getProperty(key)!!.trim().toLong()
    override fun getDouble(key: String): Double = _config.getProperty(key)!!.trim().toDouble()
    override fun getFloat(key: String): Float = _config.getProperty(key)!!.trim().toFloat()


    /**
     * The reference to the raw underlying config
     *
     * @return
     */
    override val rawConfig: Any = _config


    /**
     * The origin file path of the config
     * @return
     */
    override fun origin(): String = _fileName ?: ""


    /**
     * Loads config from the file path supplied
     *
     * @param file
     * @return
     */
    override fun loadFrom(file: String?): ConfigBase? = ConfFuncs.load(file, _enc)


    fun getInternal(key: String): Any? {
        return if (containsKey(key)) {
            val value = _config.getProperty(key)
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
}


