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
        private val config: Conf,
        private val configParent: Conf,
        private val path: String,
        private val enc: Encryptor? = null
) : Conf({ raw -> enc?.decrypt(raw) ?: raw }) {

    constructor(configPath: String, configParentPath: String, enc: Encryptor?) :
            this(Config(configPath, enc, ConfFuncs.loadPropertiesFrom(configPath)),
                 Config(configParentPath, enc, ConfFuncs.loadPropertiesFrom(configParentPath)),
                    configPath, enc)

    constructor(config: Conf, configParent: Conf, enc: Encryptor?) :
            this(config, configParent, config.origin(), enc)

    constructor(configPath: String, configParent: Conf, enc: Encryptor?) :
            this(Config(configPath, enc, ConfFuncs.loadPropertiesFrom(configPath)), configParent, configPath, enc)

    override val raw: Any = config
    override fun get(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = containsKeyInternal(key)
    override fun size(): Int = config.size()

    override fun getString(key: String): String = getInternalString(key) ?: ""
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
    override val rawConfig: Any = config

    /**
     * The origin file path of the config
     * @return
     */
    override fun origin(): String = this.path

    /**
     * Loads config from the file path supplied
     *
     * @param file
     * @return
     */
    override fun loadFrom(file: String?): Conf? = ConfFuncs.load(file, enc)

    private fun containsKeyInternal(key: String): Boolean {
        return config.containsKey(key) || configParent.containsKey(key)
    }

    private fun getInternal(key: String): Any? {
        val value = if (config.containsKey(key)) {
            config.get(key)
        } else if (configParent.containsKey(key)) {
            configParent.get(key)
        } else {
            null
        }
        return if (value != null && value is String) {
            value.trim()
        } else {
            value
        }
    }

    private fun getInternalString(key: String): String? {
        val value = if (config.containsKey(key)) {
            config.getString(key)
        } else if (configParent.containsKey(key)) {
            configParent.getString(key)
        } else {
            null
        }
        return if (value != null && value is String) {
            value.trim()
        } else {
            value
        }
    }

    private fun getStringRaw(key: String): String = getInternalString(key)?.trim() ?: ""
}