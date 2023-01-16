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
