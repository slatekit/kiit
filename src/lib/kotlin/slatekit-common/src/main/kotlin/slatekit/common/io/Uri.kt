package slatekit.common.io

import java.nio.file.Paths


/**
 * Represents a File or Directory using a simple form of a Uniform Resource Identifier ( URI ) approach
 * 1. user dir: usr://{path} or ~/{path}
 * 2. curr dir: cur://{path} or ./{path}
 * 3. path dir: abs://{path} or /{path}
 * 4. temp dir: tmp://{path} or $TMPDIR/{path}
 * 5. conf dir: cfg://{path} or ./conf/{path}
 * 6. jars dir: jar://{path} or app.jar/resources/{path}
 * @param root: The root               e.g. "user | curr | path | temp | conf | jars"
 * @param path  : The path to file     e.g. "user://company/app1/conf/env.conf
 */
data class Uri internal constructor(val raw: String,
                                    val root: Alias,
                                    val path: String?,
                                    val full: String) {

   internal constructor(raw:String, root: Alias, path:String?): this(raw, root, path, resolve(root, path))


    //val full = Paths.get(Alias.resolve(root), path)


    fun isEmpty(): Boolean = path.isNullOrEmpty()

    fun combine(otherPath: String): Uri {
        return when {
            path.isNullOrEmpty() -> {
                this.copy(
                        path = otherPath,
                        full = java.nio.file.Paths.get(full, otherPath).toString()
                )
            }
            else -> {
                this.copy(
                        path = java.nio.file.Paths.get(path, otherPath).toString(),
                        full = java.nio.file.Paths.get(full, otherPath).toString()
                )
            }
        }
    }

    fun toFile(): java.io.File {
        return when (root) {
            is Alias.Abs -> java.io.File(full)
            is Alias.Cur -> java.io.File(full)
            is Alias.Rel -> java.io.File(full)
            is Alias.Cfg -> java.io.File(full)
            is Alias.Usr -> java.io.File(full)
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
            val clean = Uris.clean(raw)
            val trim = Uris.trim(clean)
            return Uris.build(alias, raw, trim, lookup)
        }


        fun resolve(root:Alias, path:String?):String {
            return when(path) {
                null -> Paths.get(Alias.resolve(root)).toString()
                else -> Paths.get(Alias.resolve(root), path).toString()
            }
        }
    }
}