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

package slatekit.common

data class Credentials(
        val id: String = "",
        val name: String = "",
        val email: String = "",
        val key: String = "",
        val env: String = "",
        val region: String = ""
) {
    companion object CredentialsStatic {
        val empty = Credentials()
    }
}
