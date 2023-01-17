/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
 *  </kiit_header>
 */

package kiit.common.info

data class Info(
    @JvmField val about: About,
    @JvmField val build: Build,
    @JvmField val host : Host,
    @JvmField val lang : Lang
) {


    /**
     * Iterates through each of the properties
     */
    fun each(callback: (String, String) -> Unit, maxLenField: Int = 0) {
        val maxLen = Math.max(maxLenField, "lang.versionNum  ".length)
        callback("app.area         ".padEnd(maxLen), about.area)
        callback("app.name         ".padEnd(maxLen), about.name)
        callback("app.desc         ".padEnd(maxLen), about.desc)
        callback("app.tags         ".padEnd(maxLen), about.tags)
        callback("app.region       ".padEnd(maxLen), about.region)
        callback("app.contact      ".padEnd(maxLen), about.contact)
        callback("app.url          ".padEnd(maxLen), about.url)

        // Commit info
        callback("build.version    ", build.version)
        callback("build.commit     ", build.commit)
        callback("build.date       ", build.date)

        // Host
        callback("host.name        ".padEnd(maxLen), host.name)
        callback("host.ip          ".padEnd(maxLen), host.ip)
        callback("host.origin      ".padEnd(maxLen), host.origin)
        callback("host.version     ".padEnd(maxLen), host.version)

        // Lang
        callback("lang.name        ".padEnd(maxLen), lang.name)
        callback("lang.version     ".padEnd(maxLen), lang.version)
        callback("lang.versionNum  ".padEnd(maxLen), lang.vendor)
        callback("lang.java        ".padEnd(maxLen), lang.origin)
        callback("lang.home        ".padEnd(maxLen), lang.home)
    }


    companion object {
        @JvmStatic
        val none = Info(About.none, Build.empty, Host.local(), Lang.kotlin())

        fun of(about: About, build: Build? = Build.empty): Info {
            return Info(about, build ?: Build.empty, Host.local(), Lang.kotlin())
        }
    }
}