package slatekit.common.io

import slatekit.common.types.Doc
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
        return when {
            // Using directory scheme/alias e.g. ~/ ./ ../
            raw.startsWith(Alias.Abs.value) -> Uri(raw, Alias.Abs, substringOrNull(raw, Alias.Abs.value.length))
            raw.startsWith(Alias.Usr.value) -> Uri(raw, Alias.Usr, substringOrNull(raw, Alias.Usr.value.length))
            raw.startsWith(Alias.Cur.value) -> Uri(raw, Alias.Cur, substringOrNull(raw, Alias.Cur.value.length))
            raw.startsWith(Alias.Rel.value) -> Uri(raw, Alias.Rel, substringOrNull(raw, Alias.Rel.value.length))
            raw.startsWith(Alias.Cfg.value) -> Uri(raw, Alias.Cfg, substringOrNull(raw, Alias.Cfg.value.length))
            raw.startsWith(Alias.Tmp.value) -> Uri(raw, Alias.Tmp, substringOrNull(raw, Alias.Tmp.value.length))

            // Using uri based approach : e.g. abs:// usr://
            raw.startsWith(Alias.Abs.name) -> parse(Alias.Abs, raw)
            raw.startsWith(Alias.Usr.name) -> parse(Alias.Usr, raw)
            raw.startsWith(Alias.Cur.name) -> parse(Alias.Cur, raw)
            raw.startsWith(Alias.Rel.name) -> parse(Alias.Rel, raw)
            raw.startsWith(Alias.Cfg.name) -> parse(Alias.Cfg, raw)
            raw.startsWith(Alias.Jar.name) -> parse(Alias.Jar, raw)
            raw.startsWith(Alias.Tmp.name) -> parse(Alias.Tmp, raw)
            else -> {
                val ndx = raw.indexOf("/")
                if(ndx == -1){
                    Uri(raw, Alias.Other(""), raw)
                } else {
                    Uri(raw, Alias.Other(raw.substring(0, ndx)), raw.substring(ndx + 1))
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

    private fun parse(alias: Alias, raw:String):Uri {

        val path = if(raw.contains("://")) {
            substringOrNull(raw, alias.name.length + 3)
        } else {
            substringOrNull(raw, alias.name.length)
        }
        return Uri(raw, alias, path)
    }
}