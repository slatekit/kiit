package slatekit.common.io

import slatekit.common.content.Doc
import java.io.File


/**
 * Represents a File or Directory using a simple form of a Uniform Resource Identifier ( URI ) approach
 * 1. user scheme: user://{path} -> ~/{path}
 * 2. curr scheme: curr://{path} -> ./{path}
 * 3. path scheme: path://{path} -> /{path}
 * 4. temp scheme: temp://{path} -> $TMPDIR/{path}
 * 5. conf scheme: conf://{path} -> ./conf/{path}
 * 6. jars scheme: jars://{path} -> app.jar/resources/{path}
 * @param scheme: The scheme           e.g. "user | curr | path | temp | conf | jars"
 * @param path  : The path to file     e.g. "user://company/app1/conf/env.conf
 */
data class Uri internal constructor(val raw:String,
                                    val scheme: Scheme,
                                    val path:String?) {

    fun combine(otherPath:String):Uri {
        return when {
            path.isNullOrEmpty() -> this.copy(path = otherPath)
            else -> this.copy(path = java.nio.file.Paths.get(path, otherPath).toString())
        }
    }

    fun toFile(): java.io.File {
        return when (scheme) {
            is Scheme.Abs -> java.io.File(scheme.value, path)
            is Scheme.Cur -> java.io.File(scheme.value, path)
            is Scheme.Rel -> java.io.File(scheme.value, path)
            is Scheme.Cfg -> java.io.File(scheme.value, path)
            is Scheme.Usr -> java.io.File(System.getProperty("user.home"), path)
            is Scheme.Tmp -> java.io.File(System.getProperty("java.io.tmpdir"), path)
            is Scheme.Jar -> java.io.File(this.javaClass.getResource("/$path").file)
            else -> java.io.File(path)
        }
    }

    companion object {

        /**
         * Loads a directory by interpreting the path e.g.
         * 1. /     -> absolute
         * 2. ~/    -> user dir
         * 3. ./    -> curr dir
         * 4. ../   -> relative dir
         * 5. cfg/   -> ./conf dir
         * 6. $temp -> temp dir
         * 7. jar/  -> jar resources/
         */
        fun parse(raw:String):Uri {
            return when {
                raw.startsWith(Scheme.Abs.name) -> Uri(raw, Scheme.Abs, substringOrNull(raw,1))
                raw.startsWith(Scheme.Usr.name) -> Uri(raw, Scheme.Usr, substringOrNull(raw,2))
                raw.startsWith(Scheme.Cur.name) -> Uri(raw, Scheme.Cur, substringOrNull(raw,2))
                raw.startsWith(Scheme.Rel.name) -> Uri(raw, Scheme.Rel, substringOrNull(raw,3))
                raw.startsWith(Scheme.Cfg.name) -> Uri(raw, Scheme.Cfg, substringOrNull(raw,4))
                raw.startsWith(Scheme.Jar.name) -> Uri(raw, Scheme.Jar, substringOrNull(raw,4))
                raw.startsWith(Scheme.Tmp.name) -> Uri(raw, Scheme.Tmp, substringOrNull(raw,5))
                else -> {
                    val ndx = raw.indexOf("/")
                    Uri(raw, Scheme.Other(raw.substring(0, ndx)), raw.substring(ndx + 1))
                }
            }
        }

        /**
         * Interprets the path URI and returns the absolute path
         * @param uri
         * @return
         */
        fun interpret(uri: String): String? = readFile(uri).toString()

        /**
         * Loads the file represented by the URI
         * @param uri
         * @return
         */
        fun readFile(uri: String): File = Uri.parse(uri).toFile()

        /**
         * Loads the file represented by the URI and reads it as a String
         * @param uri
         * @return
         */
        fun readText(uri: String): String? = readFile(uri).readText()

        /**
         * Loads the file represented by the URI and loads into as a @see[slatekit.common.content.Doc]
         * @return
         */
        fun readDoc(uri: String): Doc? = buildDoc(readFile(uri))

        private fun buildDoc(file: File): Doc {
            val content = file.readText()
            return Doc.text(file.name, content)
        }

        private fun substringOrNull(text:String, start:Int):String? {
            return if(start >= text.length) null else text.substring(start)
        }
    }
}