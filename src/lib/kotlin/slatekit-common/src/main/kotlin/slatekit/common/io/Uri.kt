package slatekit.common.io


/**
 * Represents a File or Directory using a simple form of a Uniform Resource Identifier ( URI ) approach
 * 1. user dir: usr://{path} or ~/{path}
 * 2. curr dir: cur://{path} or ./{path}
 * 3. path dir: abs://{path} or /{path}
 * 4. temp dir: tmp://{path} or $TMPDIR/{path}
 * 5. conf dir: cfg://{path} or ./conf/{path}
 * 6. jars dir: jar://{path} or app.jar/resources/{path}
 * @param scheme: The scheme           e.g. "user | curr | path | temp | conf | jars"
 * @param path  : The path to file     e.g. "user://company/app1/conf/env.conf
 */
data class Uri internal constructor(val raw:String,
                                    val scheme: Alias,
                                    val path:String?) {

    fun isEmpty():Boolean = path.isNullOrEmpty()

    fun combine(otherPath:String):Uri {
        return when {
            path.isNullOrEmpty() -> this.copy(path = otherPath)
            else -> this.copy(path = java.nio.file.Paths.get(path, otherPath).toString())
        }
    }

    fun toFile(): java.io.File {
        return when (scheme) {
            is Alias.Abs -> java.io.File(scheme.value, path)
            is Alias.Cur -> java.io.File(Files.currDir(), path)
            is Alias.Rel -> java.io.File(scheme.value, path)
            is Alias.Cfg -> java.io.File(java.io.File(Files.currDir(), "conf"), path)
            is Alias.Usr -> java.io.File(System.getProperty("user.home"), path)
            is Alias.Tmp -> java.io.File(System.getProperty("java.io.tmpdir"), path)
            is Alias.Jar -> java.io.File(this.javaClass.getResource("/$path").file)
            else -> java.io.File(path)
        }
    }
}