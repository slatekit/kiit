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

    companion object {
        @JvmStatic
        val none = Info(About.none, Build.empty, StartInfo.none, Sys.build())
    }
}