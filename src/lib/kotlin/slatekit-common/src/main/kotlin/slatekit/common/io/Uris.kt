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

package slatekit.common.io

import slatekit.common.content.Doc
import slatekit.common.subStringPair
import java.io.File

/**
 * URI like format for loading of files. See samples below:
 * 1. user scheme: user://{file_path} -> ~/{file_path}
 * 2. curr scheme: curr://{file_path} -> ./{file_path}
 * 3. file scheme: file://{file_path} -> /{file_path}
 * 4. temp scheme: temp://{file_path} -> $TMPDIR/{file_path}
 * 5. conf scheme: conf://{file_path} -> ./conf/{file_path}
 * 6. jars scheme: jars://{file_path} -> app.jar/resources/{file_path}
 * 7. http scheme: http://{file_path} -> http://{file_path}
 */
object Uris {

    private const val CONF_DIR = "conf"

    /**
     * Interprets the path URI and returns the absolute path
     * @param uri
     * @return
     */
    fun interpret(uri: String): String? = readFile(uri).toString()

    /**
     * Reads the URI into 2 parts, 1) Scheme, and 2) path
     */
    fun readParts(uri:String?):Pair<Scheme?, String> {
        if(uri.isNullOrEmpty()) {
            return Pair(Scheme.Jar, "")
        }

        val pathParts = uri.subStringPair("://")

        // E.g. [Scheme, String] => [Scheme.Cfg, "env.props"
        return pathParts?.let { parts: Pair<String, String> ->
            val source = Scheme.parse(parts.first)
            val path = parts.second
            Pair(source, path)
        } ?: Pair(null, uri)
    }

    /**
     * Loads the file represented by the URI
     * @param uri
     * @return
     */
    fun readFile(uri: String): File = Scheme.file(readParts(uri))

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
}
