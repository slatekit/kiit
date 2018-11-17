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

package slatekit.common.info

data class StartInfo(

    @JvmField
    val args: String = "",

    @JvmField
    val logFile: String = "{@app}-{@env}-{@date}.log",

    @JvmField
    val config: String = "{@app}.config",

    @JvmField
    val env: String = "dev",

    @JvmField
    val rootDir: String = "",

    @JvmField
    val confDir: String = ""
) {

    fun log(callback: (String, Any) -> Unit) {
        callback("args", args)
        callback("log", logFile)
        callback("config", config)
        callback("env", env)
        callback("rootDir", rootDir)
        callback("confDir", confDir)
    }

    companion object {
        @JvmStatic
        val none = StartInfo()

        @JvmStatic
        fun apply(args: String, env: String, conf: String): StartInfo =
            StartInfo(
                args = args,
                env = env,
                config = conf,
                rootDir = System.getProperty("user.dir"),
                confDir = ""
            )
    }
}