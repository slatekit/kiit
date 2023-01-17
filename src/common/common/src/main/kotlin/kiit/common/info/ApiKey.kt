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

/**
 * Represents a key for accessing an API securely.
 * @param name : "admin"
 * @param key : "123456789123456789"
 * @param rolesLookup : "admin" -> true, "dev" -> true
 */
data class ApiKey(
    @JvmField val name: String,
    @JvmField val key: String,
    @JvmField val roles: String,
    @JvmField val rolesLookup: Map<String, String>
) {

    constructor(name: String, key: String, roles: String) : this(name, key, roles,
            build(roles)
    )

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
