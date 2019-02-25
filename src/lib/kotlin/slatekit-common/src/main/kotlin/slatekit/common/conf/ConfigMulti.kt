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

import slatekit.common.Conversions
import slatekit.common.DateTime
import slatekit.common.Strings
import slatekit.common.encrypt.Encryptor
//import java.time.*
import org.threeten.bp.*
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
    : Conf({ raw -> enc?.decrypt(raw) ?: raw }) {

    constructor(configPath: String, configParentPath: String, enc: Encryptor?) :
            this(ConfFuncs.loadPropertiesFrom(configPath),
                    ConfFuncs.loadPropertiesFrom(configParentPath),
                    configPath, enc)

    constructor(config: Conf, configParent: Conf, enc: Encryptor?) :
            this(config.rawConfig as Properties,
                    configParent.rawConfig as Properties, config.origin(), enc)

    constructor(configPath: String, configParent: Conf, enc: Encryptor?) :
            this(ConfFuncs.loadPropertiesFrom(configPath),
                    configParent.rawConfig as Properties, configPath, enc)

    private val _fileName = path
    private val _enc = enc

    override val raw: Any = _config
    override fun get(key: String): Any? = getInternalString(key)
    override fun containsKey(key: String): Boolean = containsKeyInternal(key)
    override fun size(): Int = _config.values.size

    override fun getString(key: String): String = Strings.decrypt(getStringRaw(key), _encryptor)
    override fun getBool(key: String): Boolean = Conversions.toBool(getStringRaw(key))
    override fun getShort(key: String): Short = Conversions.toShort(getStringRaw(key))
    override fun getInt(key: String): Int = Conversions.toInt(getStringRaw(key))
    override fun getLong(key: String): Long = Conversions.toLong(getStringRaw(key))
    override fun getFloat(key: String): Float = Conversions.toFloat(getStringRaw(key))
    override fun getDouble(key: String): Double = Conversions.toDouble(getStringRaw(key))
    override fun getInstant(key: String): Instant = Conversions.toInstant(getStringRaw(key))
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(getStringRaw(key))
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(getStringRaw(key))
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(getStringRaw(key))
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(getStringRaw(key))
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(getStringRaw(key))
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = Conversions.toZonedDateTimeUtc(getStringRaw(key))

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
    override fun loadFrom(file: String?): Conf? = ConfFuncs.load(file, _enc)

    fun containsKeyInternal(key: String): Boolean {
        return _config.containsKey(key) || _configParent.containsKey(key)
    }

    fun getInternal(key: String): Any? {
        val value = if (_config.containsKey(key)) {
            _config.getProperty(key)
        } else if (_configParent.containsKey(key)) {
            _configParent.getProperty(key)
        } else {
            null
        }
        return if (value != null && value is String) {
            value.trim()
        } else {
            value
        }
    }

    fun getInternalString(key: String): String? {
        val value = if (_config.containsKey(key)) {
            _config.getProperty(key)
        } else if (_configParent.containsKey(key)) {
            _configParent.getProperty(key)
        } else {
            null
        }
        return if (value != null && value is String) {
            value.trim()
        } else {
            value
        }
    }

    fun getStringRaw(key: String): String = getInternalString(key)?.trim() ?: ""
}