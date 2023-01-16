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

package kiit.common.conf

import kiit.common.convert.Conversions
import kiit.common.DateTime
import kiit.common.crypto.Encryptor
//import java.time.*
import org.threeten.bp.*
import kiit.common.io.Uri

/**
 * Created by kishorereddy on 6/15/17.
 */
class ConfigMulti(
        cls:Class<*>,
        private val config: Conf,
        private val parent: Conf,
        uri: Uri,
        private val enc: Encryptor? = null
) : Conf(cls, uri, { raw -> enc?.decrypt(raw) ?: raw }) {


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
     * Loads config from the file path supplied
     *
     * @param file
     * @return
     */
    override fun loadFrom(file: String?): Conf? = Confs.load(cls, file, enc)

    private fun containsKeyInternal(key: String): Boolean {
        return config.containsKey(key) || parent.containsKey(key)
    }

    private fun getInternal(key: String): Any? {
        val value = if (config.containsKey(key)) {
            config.get(key)
        } else if (parent.containsKey(key)) {
            parent.get(key)
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
        } else if (parent.containsKey(key)) {
            parent.getString(key)
        } else {
            null
        }
        return value?.trim() ?: value
    }

    private fun getStringRaw(key: String): String = getInternalString(key)?.trim() ?: ""
}