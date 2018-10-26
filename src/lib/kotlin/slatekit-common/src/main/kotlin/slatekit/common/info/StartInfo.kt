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
        val args: String = "",
        val logFile: String = "{@app}-{@env}-{@date}.log",
        val config: String = "{@app}.config",
        val env: String = "dev",
        val rootDir: String = "",
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