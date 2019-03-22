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

package slatekit.common.args

import slatekit.common.*
import slatekit.results.Try
//import java.time.*
import org.threeten.bp.*

/**
 * Container for parsed command line arguments that are either named or positional.
 *  Also holds an optional action representing a call to some method or URI in the format
 *  {area.name.action} which is expected to come before the arguments.
 *
 *  @example usage: app.users.invite -email="john@gmail.com" -role="guest"
 *
 * @param line : the raw line ( if available )
 * @param raw : the raw text that was parsed into arguments.
 * @param action : "app.users.invite"
 * @param actionParts : ["app", "users", "invite" ]
 * @param prefix : the letter used to prefix each key / name of named parameters e.g. "-"
 * @param separator : the letter used to separate the key / name with value e.g. ":"
 * @param _namedArgs : the map of named arguments ( key / value ) pairs
 * @param _indexArgs : the list of positional arguments ( index based )
 */

class Args(
        val line: String,
        override val raw: List<String>,
        val action: String,
        val actionParts: List<String>,
        val prefix: String = "-",
        val separator: String = "=",
        private val _namedArgs: Map<String, String>? = null,
        private val _metaArgs: Map<String, String>? = null,
        private val _sysArgs: Map<String, String>? = null,
        private val _indexArgs: List<String>? = null,
        private val _decryptor: ((String) -> String)?
) : Inputs {

    private val _metaIndex = 0

    /**
     * gets read-only map of key-value based arguments
     *
     * @return
     */
    val named: Map<String, String> = _namedArgs ?: mapOf()

    /**
     * gets read-only map of key-value based arguments
     *
     * @return
     */
    val meta: Map<String, String> = _metaArgs ?: mapOf()

    /**
     * gets read-only map of key-value based arguments
     *
     * @return
     */
    val sys: Map<String, String> = _sysArgs ?: mapOf()

    /**
     * gets read-only list of index/positional based arguments.
     *
     * @return
     */
    val positional: List<String> = _indexArgs ?: listOf()

    /**
     * gets the size of all the arguments ( named + positional )
     *
     * @return
     */
    override fun size(): Int = (_namedArgs?.size ?: 0) + (_indexArgs?.size ?: 0)

    /**
     * True if there are 0 arguments.
     *
     * @return
     */
    val isEmpty: Boolean = named.isEmpty() && positional.isEmpty()

    /**
     * returns true if there is only 1 argument with value:  --version -version /version
     * which shows the version of the task
     *
     * @return
     */
    val isVersion: Boolean = ArgsFuncs.isVersion(positional, _metaIndex)

    /**
     * Returns true if there is only 1 positional argument with value: pause -pause /pause
     * This is useful when running a program and then giving time to attach a debugger
     *
     * @return
     */
    val isPause: Boolean = ArgsFuncs.isPause(positional, _metaIndex)

    /**
     * Returns true if there is only 1 positional argument with value: exit -exit /exit
     * This is useful when running a program and then giving time to attach a debugger
     *
     * @return
     */
    val isExit: Boolean = ArgsFuncs.isExit(positional, _metaIndex)

    /**
     * returns true if there is only 1 argument with value: --help -help /? -? ?
     *
     * @return
     */
    val isHelp: Boolean = ArgsFuncs.isHelp(positional, _metaIndex)

    /**
     * returns true if there is only 1 argument with value -about or -info
     *
     * @return
     */
    val isInfo: Boolean = ArgsFuncs.isMetaArg(positional, _metaIndex, "about", "info")

    /**
     * gets the verb at the supplied position
     * @param pos
     * @return
     */
    fun getVerb(pos: Int): String = getListValueOrElse(actionParts, pos, "")

    /**
     * get the value of the indexed argumentd ( after named arguments ) at the supplied pos
     * @param pos
     * @return
     */
    fun getValueAt(pos: Int): String = getListValueOrElse(_indexArgs, pos, "")

    /**
     * whether or not this contains the key in the meta args
     */
    fun containsMetaKey(key: String): Boolean = _metaArgs?.let { meta -> meta.containsKey(key) } ?: false

    /**
     * whether or not this contains the key in the meta args
     */
    fun containsSysKey(key: String): Boolean = _sysArgs?.let { sys -> sys.containsKey(key) } ?: false

    /**
     * gets a string from the meta args
     */
    fun getMetaString(key: String): String? {
        return if (containsMetaKey(key)) {
            _metaArgs?.let { m -> m[key] } ?: ""
        } else {
            null
        }
    }

    /**
     * gets a string from the meta args
     */
    fun getMetaStringOrElse(key: String, defaultValue: String): String {
        return if (containsMetaKey(key)) {
            _metaArgs?.let { m -> m[key] } ?: ""
        } else {
            defaultValue
        }
    }

    /**
     * gets a string from the meta args
     */
    fun getSysString(key: String): String? {
        return if (containsSysKey(key)) {
            _sysArgs?.let { m -> m[key] } ?: ""
        } else {
            null
        }
    }

    /**
     * gets a string from the meta args
     */
    fun getSysStringOrElse(key: String, defaultValue: String): String {
        return if (containsSysKey(key)) {
            _sysArgs?.let { m -> m[key] } ?: ""
        } else {
            defaultValue
        }
    }

    /**
     * Gets a value in the list at the supplied position or returns the default value
     * @param items
     * @param pos
     * @param default
     * @tparam T
     * @return
     */
    fun getListValueOrElse(items: List<String>?, pos: Int, default: String): String =
            if (pos < 0 || pos >= items?.size ?: 0)
                default
            else
                items?.get(pos) ?: default

    /**
     * Methods to get basic values
     * NOTE: There are other methods for getStringOpt, and getStringOrElse
     * @param key
     * @return
     */
    override fun getString(key: String): String = Strings.decrypt(named[key] ?: "", _decryptor)

    override fun getBool(key: String): Boolean = Conversions.toBool(named[key] ?: "false")
    override fun getShort(key: String): Short = Conversions.toShort(named[key] ?: "0")
    override fun getInt(key: String): Int = Conversions.toInt(named[key] ?: "0")
    override fun getLong(key: String): Long = Conversions.toLong(named[key] ?: "0")
    override fun getFloat(key: String): Float = Conversions.toFloat(named[key] ?: "0")
    override fun getDouble(key: String): Double = Conversions.toDouble(named[key] ?: "0")
    override fun getInstant(key: String): Instant = Conversions.toInstant(named[key] ?: "0")
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(named[key] ?: "")
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(named[key] ?: "")
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(named[key] ?: "")
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(named[key] ?: "")
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(named[key] ?: "")
    override fun getZonedDateTimeUtc(key: String): ZonedDateTime = Conversions.toZonedDateTimeUtc(named[key] ?: "")

    override fun get(key: String): Any? = if (named.contains(key)) named[key] else null
    //override fun getObject(key: String): Any? = if (named.contains(key)) named[key] else null
    override fun containsKey(key: String): Boolean = _namedArgs?.contains(key) ?: false

    fun hasMetaArgs(): Boolean = _metaArgs?.isNotEmpty() ?: false

    companion object {

        @JvmStatic
        fun default(): Args = Args("", listOf(), "", listOf(), _decryptor = null)

        /**
         * Parses the arguments using the supplied prefix and separator for the args.
         * e.g. users.activate -email:kishore@gmail.com -code:1234
         *
         * @param line : the raw line of text to parse into {action} {key/value}* {position}*
         * @param prefix : the prefix for a named key/value pair e.g. "-" as in -env:dev
         * @param sep : the separator for a nmaed key/value pair e.g. ":" as in -env:dev
         * @param hasAction: whether the line of text has an action before any named args.
         * @param metaChar : the prefix to designate arguments as meta arguments which are saved in the named collection
         * @param sysChar : the prefix to designate arguments as sys arguments which are saved in the sys collection
         * @return
         */
        @JvmStatic
        fun parse(
                line: String,
                prefix: String = "-",
                sep: String = ":",
                hasAction: Boolean = false,
                metaChar: String = "@",
                sysChar: String = "$"
        ): Try<Args> {
            return ArgsService().parse(line, prefix, sep, hasAction, metaChar, sysChar)
        }

        /**
         * Parses the arguments using the supplied prefix and separator for the args.
         * e.g. users.activate -email:kishore@gmail.com -code:1234
         *
         * @param args : the raw line of text to parse into {action} {key/value}* {position}*
         * @param prefix : the prefix for a named key/value pair e.g. "-" as in -env:dev
         * @param sep : the separator for a nmaed key/value pair e.g. ":" as in -env:dev
         * @param hasAction: whether the line of text has an action before any named args.
         * @param metaChar : the prefix to designate arguments as meta arguments which are saved in the named collection
         * @param sysChar : the prefix to designate arguments as sys arguments which are saved in the sys collection
         * @return
         */
        @JvmStatic
        fun parseArgs(
                args: Array<String>,
                prefix: String = "-",
                sep: String = ":",
                hasAction: Boolean = false,
                metaChar: String = "@",
                sysChar: String = "$"
        ): Try<Args> {
            // build a single line from args
            val line = if (args.isNotEmpty()) {
                args.joinToString(" ")
            } else
                ""
            return ArgsService().parse(line, prefix, sep, hasAction, metaChar, sysChar)
        }
    }
}