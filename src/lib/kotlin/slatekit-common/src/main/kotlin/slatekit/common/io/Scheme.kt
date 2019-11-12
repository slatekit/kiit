package slatekit.common.io


/**
 * Represents an alias for a Uri representing a directory or file on a machine
 */
sealed class Scheme(val name: String, val value:String) {

    /**
     * Indicates file from a specified path
     */
    object Abs : Scheme("abs", "/")

    /**
     * Indicates file from user directory e.g. ~/ | "user"
     */
    object Usr : Scheme("usr", "~/")

    /**
     * Indicates file from current directory
     */
    object Cur : Scheme("cur", "./")

    /**
     * Indicates file from a specified path
     */
    object Rel : Scheme("rel", "../")

    /**
     * Indicates file from a specified path
     */
    object Cfg : Scheme("cfg", "./conf")

    /**
     * Indicates file from temp directory
     */
    object Tmp : Scheme("tmp", "\$temp")

    /**
     * Indicates file from Java resources in jar file
     */
    object Jar : Scheme("jar", "jar")

    /**
     * Indicates a file from some other source
     * @param m
     */
    class Other(m: String) : Scheme(m, m)

    companion object {

        /**
         * Parse the text for a matching Scheme type
         */
        @JvmStatic
        fun parse(text: String): Scheme = when (text.trim().toLowerCase()) {
            Scheme.Abs.name -> Scheme.Abs
            Scheme.Usr.name -> Scheme.Usr
            Scheme.Cur.name -> Scheme.Cur
            Scheme.Rel.name -> Scheme.Rel
            Scheme.Cfg.name -> Scheme.Cfg
            Scheme.Jar.name -> Scheme.Jar
            Scheme.Tmp.name -> Scheme.Tmp
            else -> Scheme.Other(text)
        }
    }
}