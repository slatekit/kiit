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

package slatekit.apis

data class ApiReg(val api: ApiBase,
                  val declaredOnly: Boolean = true,
                  val roles: String? = null,
                  val auth: String? = null,
                  val protocol: String? = "*"
)