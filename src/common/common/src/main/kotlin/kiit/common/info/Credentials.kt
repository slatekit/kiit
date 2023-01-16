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

package kiit.common.info

data class Credentials(
    @JvmField val id: String = "",
    @JvmField val name: String = "",
    @JvmField val email: String = "",
    @JvmField val key: String = "",
    @JvmField val env: String = "",
    @JvmField val region: String = "",
    @JvmField val roles: String = ""
) {
    companion object {
        @JvmStatic
        val empty = Credentials()
    }
}
