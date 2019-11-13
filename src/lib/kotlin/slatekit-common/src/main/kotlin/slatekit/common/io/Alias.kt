package slatekit.common.io


/**
 * Represents an alias for a Uri representing a directory or file on a machine
 */
sealed class Alias(val name: String, val value:String) {

    /**
     * Indicates file from a specified path
     */
    object Abs : Alias("abs", "/")

    /**
     * Indicates file from user directory e.g. ~/ | "user"
     */
    object Usr : Alias("usr", "~/")

    /**
     * Indicates file from current directory
     */
    object Cur : Alias("cur", "./")

    /**
     * Indicates file from a specified path
     */
    object Rel : Alias("rel", "../")

    /**
     * Indicates file from a specified path
     */
    object Cfg : Alias("cfg", "./conf")

    /**
     * Indicates file from temp directory
     */
    object Tmp : Alias("tmp", "\$temp")

    /**
     * Indicates file from Java resources in jar file
     */
    object Jar : Alias("jar", "jar")

    /**
     * Indicates a file from some other source
     * @param m
     */
    class Other(m: String) : Alias(m, m)

    companion object {

        /**
         * Parse the text for a matching Alias type
         */
        @JvmStatic
        fun parse(text: String): Alias = when (text.trim().toLowerCase()) {
            Alias.Abs.name -> Alias.Abs
            Alias.Usr.name -> Alias.Usr
            Alias.Cur.name -> Alias.Cur
            Alias.Rel.name -> Alias.Rel
            Alias.Cfg.name -> Alias.Cfg
            Alias.Jar.name -> Alias.Jar
            Alias.Tmp.name -> Alias.Tmp
            else -> Alias.Other(text)
        }
    }
}