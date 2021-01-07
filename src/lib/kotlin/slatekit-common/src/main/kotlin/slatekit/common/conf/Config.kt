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

import slatekit.common.*
import slatekit.common.crypto.Encryptor
//import java.time.*
import org.threeten.bp.*
import slatekit.common.convert.Conversions
import slatekit.common.ext.decrypt
import slatekit.common.ext.getEnv
import slatekit.common.io.Uri
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
 * @param props
 */
class Config(
        cls: Class<*>,
        uri:Uri,
        val config: Properties,
        val enc: Encryptor? = null
)
    : Conf(cls, uri, { raw -> enc?.decrypt(raw) ?: raw }) {


    /**
     * Get or load the config object
     */
    override val raw: Any = config
    override fun get(key: String): Any? = getInternal(key)
    override fun containsKey(key: String): Boolean = config.containsKey(key)
    override fun size(): Int = config.values.size

    override fun getString(key: String): String = interpret(key)
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


    /**
     * Extends the config by supporting decryption via marker tags.
     * e.g.
     *  db.connection = "@{decrypt('8r4AbhQyvlzSeWnKsamowA')}"
     *  db.connection = "@{env('APP1_DB_URL')}"
     *
     * @param key : key: The name of the config key
     * @return
     */
    private fun interpret(key: String): String {
        val raw = getStringRaw(key)
        val value = if(raw.startsWith("@{env")) {
            raw.getEnv()
        }
        else if(raw.startsWith("@{decrypt")) {
            raw.decrypt(encryptor)
        } else {
            raw
        }
        return value
    }


    private fun getInternal(key: String): Any? {
        return if (containsKey(key)) {
            val value = config.getProperty(key)
            if (value != null && value is String) {
                value.trim()
            } else {
                value
            }
        } else {
            null
        }
    }


    private fun getStringRaw(key: String): String = config.getProperty(key)?.trim() ?: ""

    companion object {

        operator fun invoke(cls:Class<*>):Config {
            val info = Props.fromPath(cls, "")
            return Config(cls, info.first, info.second)
        }


        fun of(cls:Class<*>, configPath: String, enc: Encryptor? = null):Config {
            val info = Props.fromPath(cls, configPath)
            val conf = Config(cls, info.first, info.second, enc)
            return conf
        }


        fun of(cls:Class<*>, configPath: String, configParentPath: String, enc: Encryptor?):ConfigMulti {
            val parentInfo = Props.fromPath(cls, configParentPath)
            val parentConf = Config(cls, parentInfo.first, parentInfo.second, enc)

            val inheritInfo = Props.fromPath(cls, configPath)
            val inheritConf = Config(cls, inheritInfo.first, inheritInfo.second, enc)

            val conf = ConfigMulti(cls, inheritConf, parentConf, inheritInfo.first, enc)
            return conf
        }


        fun of(cls:Class<*>, configSource: Uri, configParent: Conf, enc: Encryptor?) :ConfigMulti {
            val inheritProps = Props.fromUri(cls, configSource)
            val inheritConf = Config(cls, configSource, inheritProps, enc)
            val conf = ConfigMulti(cls, inheritConf, configParent, configSource, enc)
            return conf
        }
    }
}

