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

package slatekit.apis.support

/**
 * This is essentially a copy of the Api annotation at runtime.
 * This is needed because Kotlin annotation can not be instantiated,
 * and we need to make a copy of the original api annotation on a
 * class with possible overrides.
 */

data class ApiInfo(val area: String = "",
                   val name: String = "",
                   val desc: String = "",
                   val roles: String = "",
                   val auth: String = "app",
                   val verb: String = "get",
                   val protocol: String = "*")