package kiit.common.io

import java.io.File


/**
 * Represents an alias for a Uri pointing to a directory or file on a machine
 * This can be used in the URI as part of the "root"
 * e.g.
 *
 * abs:/users/kishore/dev/slatekit
 * usr:/dev/slatekit                => ~/dev/slatekit
 * cur:/dev/slatekit                => ~/dev/slatekit
 * rel:/dev/slatekit                => ~/dev/slatekit
 * cfg:/dev/slatekit                => ~/dev/slatekit
 * :/dev/slatekit                   => ~/dev/slatekit
 */
sealed class Alias(val name: String, val value:String) {

    /**
     * Indicates file from a specified absolute path
     */
    object Abs : Alias("abs", File.separator)

    /**
     * Indicates file from user directory e.g. ~/ | "user"
     */
    object Usr : Alias("usr", "~")

    /**
     * Indicates file from current directory
     */
    object Cur : Alias("cur", ".")

    /**
     * Indicates file from Java resources in jar file
     */
    object Jar : Alias("jar", "jar")

    /**
     * Indicates config directory from current directory of executing app
     * This is useful for deployed applications that have their settings
     * in the .conf directory for configuration after install/deployment
     */
    object Cfg : Alias("cfg", "conf")

    /**
     * Indicates reference to another directory using a name e.g. $temp, $HOME,
     * this could be a reference to an Environment variable or some other managed
     * variable.
     */
    object Ref : Alias("tmp", "$")

    /**
     * Indicates file from a specified path
     */
    object Rel : Alias("rel", "..")

    /**
     * Indicates file from a specified path
     */
    class Other(m: String) : Alias(m, m)

    companion object {

        /**
         * Parse the text for a matching Alias type
         */
        @JvmStatic
        fun parse(text: String): Alias = when (text.trim().lowercase()) {
            Abs.name -> Abs
            Usr.name -> Usr
            Cur.name -> Cur
            Jar.name -> Jar
            Cfg.name -> Cfg
            Ref.name -> Ref
            Rel.name -> Rel
            else           -> Other(text)
        }

        /**
         * Parse the text for a matching Alias type
         */
        @JvmStatic
        fun resolve(alias: Alias): String {
            return when (alias) {
                Abs -> Files.absDir
                Usr -> Files.usrDir
                Cur -> Files.curDir
                Cfg -> Files.cfgDir
                else -> Files.curDir
            }
        }
    }
}