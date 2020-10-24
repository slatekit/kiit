package slatekit.common.io

import java.io.File


/**
 * Represents a File or Directory using a simple form of a Uniform Resource Identifier ( URI ) approach
 * 1. user dir: usr://{path} or ~/{path}
 * 2. curr dir: cur://{path} or ./{path}
 * 3. path dir: abs://{path} or /{path}
 * 4. temp dir: tmp://{path} or $TMPDIR/{path}
 * 5. conf dir: cfg://{path} or ./conf/{path}
 * 6. jars dir: jar://{path} or app.jar/resources/{path}
 * @param raw  : Raw path             e.g. "~/app1/conf/env.conf"
 * @param root : Root Alias           e.g. "abs | usr | cur | tmp | cfg"
 * @param path : Path to file         e.g. "app1/conf/env.conf"
 * @param full : Full path to file    e.g. "/Users/batman/app1/conf/env.conf"
 */
data class Uri internal constructor(val raw: String,
                                    val root: Alias,
                                    val path: String?,
                                    val full: String) {

   internal constructor(raw:String, root: Alias, path:String?): this(raw, root, path, resolve(root, path))


    fun isEmpty(): Boolean = path.isNullOrEmpty()

    fun combine(otherPath: String): Uri {
        return when {
            path.isNullOrEmpty() -> {
                this.copy(
                        raw = otherPath,
                        path = otherPath,
                        full = File(full, otherPath).toString()
                )
            }
            else -> {
                this.copy(
                        raw  = File(path, otherPath).toString(),
                        path = File(path, otherPath).toString(),
                        full = File(full, otherPath).toString()
                )
            }
        }
    }

    fun toFile(): java.io.File {
        return when (root) {
            is Alias.Ref -> java.io.File(System.getProperty("java.io.tmpdir"), path)
            is Alias.Jar -> java.io.File(this.javaClass.getResource("/$path").file)
            else -> java.io.File(path)
        }
    }

    companion object {

        fun abs(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Abs, path, lookup)
        fun cur(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Cur, path, lookup)
        fun rel(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Rel, path, lookup)
        fun cfg(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Cfg, path, lookup)
        fun usr(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Usr, path, lookup)
        fun tmp(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Ref, path, lookup)
        fun jar(path: String, lookup: Map<String, String>? = null): Uri = of(Alias.Jar, path, lookup)


        fun of(alias: Alias, raw: String, lookup: Map<String, String>? = null): Uri {
            val clean = clean(raw)
            val trim = trim(clean)
            return Uris.build(alias, raw, trim, lookup)
        }

        fun clean(path:String):String {
            return when {
                File.separator == "/" -> { path.replace("\\", File.separator).replace("\\\\", File.separator) }
                File.separator == "\\" -> { path.replace("/", File.separator).replace("//", File.separator) }
                else -> path
            }
        }

        fun trim(path:String):String {
            val trimmed = path.trim()
            return when {
                trimmed.startsWith("/") -> trimmed.substring(1)
                trimmed.startsWith("\\") -> trimmed.substring(1)
                trimmed.startsWith(File.separator) -> trimmed.substring(1)
                else -> trimmed
            }
        }

        private fun resolve(root:Alias, path:String?):String {
            return when(path) {
                null -> File(Alias.resolve(root)).toString()
                else -> File(Alias.resolve(root), path).toString()
            }
        }
    }
}