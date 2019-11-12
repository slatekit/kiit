package slatekit.common.io

import java.io.File


/**
 * Represents the "scheme" portion of a URI e.g. "http" as in "http://google.com"
 * However, this ADT is intended to indicate the source directory/location of a file reference.
 */
sealed class Scheme(val name: String) {

    /**
     * Indicates file from user directory e.g. ~/ | "user"
     */
    object Usr : Scheme("user")

    /**
     * Indicates file from temp directory
     */
    object Tmp : Scheme("temp")

    /**
     * Indicates file from Java resources in jar file
     */
    object Jar : Scheme("jars")

    /**
     * Indicates file from a "conf" sub-directory
     * in the same location as current working directory
     */
    object Cfg : Scheme("conf")

    /**
     * Indicates file from current directory
     */
    object Curr : Scheme("curr")

    /**
     * Indicates file from a specified path
     */
    object Path : Scheme("path")

    /**
     * Indicates file from the http
     */
    object Http : Scheme("http")

    /**
     * Indicates file from the https
     */
    object Https : Scheme("https")

    /**
     * Indicates a file from some other source
     * @param m
     */
    class Other(m: String) : Scheme(m)

    companion object {

        @JvmStatic
        fun file(parts:Pair<Scheme?, String>):File {
            val (scheme, path) = parts
            return file(scheme, path)
        }

        @JvmStatic
        fun file(scheme: Scheme?, path:String):File {
            return when (scheme) {
                null           -> File(path)
                is Scheme.Usr  -> File(System.getProperty("user.home"), path)
                is Scheme.Curr -> File(".", path)
                is Scheme.Path -> File("/", path)
                is Scheme.Tmp  -> File(System.getProperty("java.io.tmpdir"), path)
                is Scheme.Jar  -> File(this.javaClass.getResource("/$path").file)
                is Scheme.Cfg  -> File("./conf", path)
                else -> File(path)
            }
        }

        /**
         * Parse the text for a matching Scheme type
         */
        @JvmStatic
        fun parse(text: String): Scheme = when (text.trim().toLowerCase()) {
            "~", Scheme.Usr.name  -> Scheme.Usr
            ".", Scheme.Curr.name -> Scheme.Curr
            "/", Scheme.Path.name -> Scheme.Path
            Scheme.Tmp.name -> Scheme.Tmp
            Scheme.Jar.name -> Scheme.Jar
            Scheme.Cfg.name -> Scheme.Cfg
            Scheme.Http.name -> Scheme.Http
            Scheme.Https.name -> Scheme.Https
            else -> Scheme.Other(text)
        }
    }
}