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

/**
 * Represents a key for accessing an API securely.
 * @param name : "admin"
 * @param key  : "123456789123456789"
 * @param rolesLookup : "admin" -> true, "dev" -> true
 */
data class ApiKey(
    val name: String,
    val key: String,
    val roles: String,
    val rolesLookup: Map<String, String>
) {

    constructor(name: String, key: String, roles: String) : this(name, key, roles, build(roles))


    companion object {

        @JvmStatic
        fun build(roles: String?): Map<String, String> {
            return roles?.splitToSequence(',')?.associateBy({ it }, { it }) ?: mapOf()
        }


        @JvmStatic
        fun build(name: String, key: String, roles: String?): ApiKey {
            val lookup = roles?.splitToSequence(',')?.associateBy({ it }, { it }) ?: mapOf()
            return ApiKey(name, key, roles.orEmpty(), lookup)
        }
    }

}
