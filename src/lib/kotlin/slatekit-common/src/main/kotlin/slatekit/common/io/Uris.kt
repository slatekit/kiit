package slatekit.common.io

import slatekit.common.types.ContentFile
import slatekit.common.types.ContentFiles
import java.io.File


/**
 * Represents a File or Directory using a simple form of a Uniform Resource Identifier ( URI ) approach
 * 1. user root: usr://{path} -> ~/{path}
 * 2. curr root: cur://{path} -> ./{path}
 * 3. path root: abs://{path} -> /{path}
 * 4. temp root: tmp://{path} -> $TMPDIR/{path}
 * 5. conf root: cfg://{path} -> ./conf/{path}
 * 6. jars root: jar://{path} -> app.jar/resources/{path}
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
        return parse(raw, null)
    }


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
    fun parse(raw:String, lookup:Map<String, String>? = null):Uri {
        val path = raw.trim()
        return when {


            // Using directory root/alias e.g. ~/ ./ ../
            path.startsWith(Alias.Abs.value) -> Uri(path, Alias.Abs, substringOrNull(path, Alias.Abs.value.length))
            path.startsWith(Alias.Usr.value) -> Uri(path, Alias.Usr, substringOrNull(path, Alias.Usr.value.length))
            path.startsWith(Alias.Cur.value) -> Uri(path, Alias.Cur, substringOrNull(path, Alias.Cur.value.length))
            path.startsWith(Alias.Rel.value) -> Uri(path, Alias.Rel, substringOrNull(path, Alias.Rel.value.length))

            path == Alias.Cfg.value -> Uri(path, Alias.Cfg, null)
            path.startsWith(Alias.Cfg.value) -> Uri(path, Alias.Cfg, substringOrNull(path, Alias.Cfg.value.length))

            // Using uri based approach : e.g. abs:// usr://
            path.startsWith(Alias.Abs.name + "://", ignoreCase = true) -> parse(Alias.Abs, path, lookup)
            path.startsWith(Alias.Usr.name + "://", ignoreCase = true) -> parse(Alias.Usr, path, lookup)
            path.startsWith(Alias.Cur.name + "://", ignoreCase = true) -> parse(Alias.Cur, path, lookup)
            path.startsWith(Alias.Rel.name + "://", ignoreCase = true) -> parse(Alias.Rel, path, lookup)
            path.startsWith(Alias.Cfg.name + "://", ignoreCase = true) -> parse(Alias.Cfg, path, lookup)
            path.startsWith(Alias.Jar.name + "://", ignoreCase = true) -> parse(Alias.Jar, path, lookup)
            //raw.startsWith(Alias.Tmp.name) -> parse(Alias.Tmp, raw)
            else -> {
                val ndx = path.indexOf("/")
                if(ndx == -1){
                    Uri(path, Alias.Other(""), path)
                } else {
                    Uri(path, Alias.Other(path.substring(0, ndx)), path.substring(ndx + 1))
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
    fun readDoc(uri: String): ContentFile? = buildDoc(readFile(uri))


    private fun buildDoc(file: File): ContentFile {
        val content = file.readText()
        return ContentFiles.text(file.name, content)
    }

    private fun substringOrNull(text:String, start:Int):String? {
        return if(start >= text.length) null else text.substring(start)
    }

    private fun parse(alias: Alias, raw:String, lookup:Map<String, String>? = null):Uri {
        val index = raw.indexOf("://")
        val rest = raw.substring(index + 3).trim()
        val path = Uri.trim(rest)
        return build(alias, raw, path, lookup)
    }


    private fun resolve(alias: Alias, lookup: Map<String, String>?):String {
        return when(alias) {
            Alias.Jar -> ""
            Alias.Cfg -> lookup?.get(Alias.Cur.value)?.let { File(it, "conf").toString() } ?: Files.cfgDir
            Alias.Rel -> lookup?.get(Alias.Cur.value)?.let { File(it, "").parent.toString() } ?: Files.relDir
            else      -> lookup?.getOrDefault(alias.value, Alias.resolve(alias)) ?: Alias.resolve(alias)
        }
    }


    fun build(alias: Alias, raw: String, path:String, lookup: Map<String, String>?):Uri {
        val root = resolve(alias, lookup)
        val full = File(root, path).toString()
        val child = File(path).toString()
        return Uri(raw, alias, Uri.clean(child), Uri.clean(full))
    }
}