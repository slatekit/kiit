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
    val host: Host,
    val lang: Lang,
    val status: Status,
    val start: StartInfo,
    val build: Build
) {

    companion object {
        @JvmStatic
        val none = Info(About.none, Host.local(), Lang.kotlin(), Status.none, StartInfo.none, Build.empty)
    }
}