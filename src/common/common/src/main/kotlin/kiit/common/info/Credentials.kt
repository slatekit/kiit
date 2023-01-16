/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
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
