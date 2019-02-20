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

data class Info(
    val about: About,
    val build: Build,
    val start: StartInfo,
    val system: Sys
) {


    /**
     * Iterates through each of the properties
     */
    fun each(callback: (String, String) -> Unit, maxLenField: Int = 0) {
        val maxLen = Math.max(maxLenField, "lang.versionNum  ".length)
        callback("app.name         ".padEnd(maxLen), about.name)
        callback("app.desc         ".padEnd(maxLen), about.desc)
        callback("app.version      ".padEnd(maxLen), about.version)
        callback("app.tags         ".padEnd(maxLen), about.tags)
        callback("app.group        ".padEnd(maxLen), about.group)
        callback("app.region       ".padEnd(maxLen), about.region)
        callback("app.contact      ".padEnd(maxLen), about.contact)
        callback("app.url          ".padEnd(maxLen), about.url)

        // Start up
        callback("args             ".padEnd(maxLen), start.args)
        callback("env              ".padEnd(maxLen), start.env)
        callback("config           ".padEnd(maxLen), start.config)
        callback("log              ".padEnd(maxLen), start.logFile)
        callback("started          ".padEnd(maxLen), start.started.toString())

        // Commit info
        callback("build.version    ", build.version)
        callback("build.commit     ", build.commit)
        callback("build.date       ", build.date)

        // Host
        callback("host.name        ".padEnd(maxLen), system.host.name)
        callback("host.ip          ".padEnd(maxLen), system.host.ip)
        callback("host.origin      ".padEnd(maxLen), system.host.origin)
        callback("host.version     ".padEnd(maxLen), system.host.version)

        // Lang
        callback("lang.name        ".padEnd(maxLen), system.lang.name)
        callback("lang.version     ".padEnd(maxLen), system.lang.version)
        callback("lang.versionNum  ".padEnd(maxLen), system.lang.vendor)
        callback("lang.java        ".padEnd(maxLen), system.lang.origin)
        callback("lang.home        ".padEnd(maxLen), system.lang.home)
    }


    companion object {
        @JvmStatic
        val none = Info(About.none, Build.empty, StartInfo.none, Sys.build())
    }
}