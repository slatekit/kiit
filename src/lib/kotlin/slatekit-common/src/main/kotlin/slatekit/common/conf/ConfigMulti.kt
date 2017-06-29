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
 * Created by kishorereddy on 6/15/17.
 */
class ConfigMulti(
        private val _config: Properties,
        private val _configParent: Properties,
        path: String,
        enc: Encryptor? = null
)
    : ConfigBase({ raw -> enc?.decrypt(raw) ?: raw }) {


    constructor(configPath: String, configParentPath: String, enc: Encryptor?) :
            this(ConfFuncs.loadPropertiesFrom(configPath),
                    ConfFuncs.loadPropertiesFrom(configParentPath),
                    configPath, enc)


    constructor(config: ConfigBase, configParent: ConfigBase, enc: Encryptor?) :
            this(config.rawConfig as Properties,
                    configParent.rawConfig as Properties, config.origin(), enc)


    private val _fileName = path
    private val _enc = enc


    override fun get(key: String): Any? = getInternalString(key)
    override fun getObject(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = containsKeyInternal(key)
    override fun size(): Int = _config.values.size


    override fun getString(key: String): String = InputFuncs.decrypt(getInternalString(key)!!.trim(), _encryptor)
    override fun getDate(key: String): DateTime = InputFuncs.convertDate(getInternalString(key)!!.trim())
    override fun getBool(key: String): Boolean = getInternalString(key)!!.trim().toBoolean()
    override fun getShort(key: String): Short = getInternalString(key)!!.trim().toShort()
    override fun getInt(key: String): Int = getInternalString(key)!!.trim().toInt()
    override fun getLong(key: String): Long = getInternalString(key)!!.trim().toLong()
    override fun getFloat(key: String): Float = getInternalString(key)!!.trim().toFloat()
    override fun getDouble(key: String): Double = getInternalString(key)!!.trim().toDouble()


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
    override fun origin(): String = _fileName


    /**
     * Loads config from the file path supplied
     *
     * @param file
     * @return
     */
    override fun loadFrom(file: String?): ConfigBase? = ConfFuncs.load(file, _enc)


    fun containsKeyInternal(key: String): Boolean {
        return _config.containsKey(key) || _configParent.containsKey(key)
    }


    fun getInternal(key: String): Any? {
        val value = if (_config.containsKey(key)) {
            _config.getProperty(key)
        }
        else if (_configParent.containsKey(key)) {
            _configParent.getProperty(key)
        }
        else {
            null
        }
        return if (value != null && value is String) {
            value.trim()
        }
        else {
            value
        }
    }


    fun getInternalString(key: String): String? {
        val value = if (_config.containsKey(key)) {
            _config.getProperty(key)
        }
        else if (_configParent.containsKey(key)) {
            _configParent.getProperty(key)
        }
        else {
            null
        }
        return if (value != null && value is String) {
            value.trim()
        }
        else {
            value
        }
    }
}