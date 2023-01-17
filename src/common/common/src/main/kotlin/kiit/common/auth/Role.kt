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

package kiit.common.auth

interface Role {
    val name: String
}

/**
 * No roles
 */
object RoleNone : Role {
    override val name = Roles.NONE
}

/**
 * Represents any authenticated user
 */
object RoleAny : Role {
    override val name = Roles.ALL
}

/**
 * Represents an guest user
 */
object RoleGuest : Role {
    override val name = Roles.GUEST
}

/**
 * Represents a reference to a parent role
 */
object RoleParent : Role {
    override val name = Roles.PARENT
}

/**
 * No roles
 */
data class NamedRole(override val name: String) : Role
