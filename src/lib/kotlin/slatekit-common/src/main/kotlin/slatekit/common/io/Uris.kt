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
object Uris {

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
        val increment = 3
        return when {
            // Using directory scheme/alias e.g. ~/ ./ ../
            raw.startsWith(Scheme.Abs.value) -> Uri(raw, Scheme.Abs, substringOrNull(raw, Scheme.Abs.value.length))
            raw.startsWith(Scheme.Usr.value) -> Uri(raw, Scheme.Usr, substringOrNull(raw, Scheme.Usr.value.length))
            raw.startsWith(Scheme.Cur.value) -> Uri(raw, Scheme.Cur, substringOrNull(raw, Scheme.Cur.value.length))
            raw.startsWith(Scheme.Rel.value) -> Uri(raw, Scheme.Rel, substringOrNull(raw, Scheme.Rel.value.length))
            raw.startsWith(Scheme.Cfg.value) -> Uri(raw, Scheme.Cfg, substringOrNull(raw, Scheme.Cfg.value.length))
            raw.startsWith(Scheme.Jar.value) -> Uri(raw, Scheme.Jar, substringOrNull(raw, Scheme.Jar.value.length))
            raw.startsWith(Scheme.Tmp.value) -> Uri(raw, Scheme.Tmp, substringOrNull(raw, Scheme.Tmp.value.length))

            // Using uri based approach : e.g. abs:// usr://
            raw.startsWith(Scheme.Abs.name + "://") -> Uri(raw, Scheme.Abs, substringOrNull(raw,Scheme.Abs.name.length + increment))
            raw.startsWith(Scheme.Usr.name + "://") -> Uri(raw, Scheme.Usr, substringOrNull(raw,Scheme.Usr.name.length + increment))
            raw.startsWith(Scheme.Cur.name + "://") -> Uri(raw, Scheme.Cur, substringOrNull(raw,Scheme.Cur.name.length + increment))
            raw.startsWith(Scheme.Rel.name + "://") -> Uri(raw, Scheme.Rel, substringOrNull(raw,Scheme.Rel.name.length + increment))
            raw.startsWith(Scheme.Cfg.name + "://") -> Uri(raw, Scheme.Cfg, substringOrNull(raw,Scheme.Cfg.name.length + increment))
            raw.startsWith(Scheme.Jar.name + "://") -> Uri(raw, Scheme.Jar, substringOrNull(raw,Scheme.Jar.name.length + increment))
            raw.startsWith(Scheme.Tmp.name + "://") -> Uri(raw, Scheme.Tmp, substringOrNull(raw,Scheme.Tmp.name.length + increment))
            else -> {
                val ndx = raw.indexOf("/")
                if(ndx == -1){
                    Uri(raw, Scheme.Other(""), raw)
                } else {
                    Uri(raw, Scheme.Other(raw.substring(0, ndx)), raw.substring(ndx + 1))
                }
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
    fun readFile(uri: String): File = Uris.parse(uri).toFile()

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