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


package slatekit.common.auth

interface RoleInfo {
    val name: String
    val value: String
}


/**
 * Represents any authenticated user
 */
object RoleAny : RoleInfo {
    override val name = "any";
    override val value = "*"
}

/**
 * Represents a guest
 */
object RoleGuest : RoleInfo {
    override val name = "guest";
    override val value = "?"
}


/**
 * Represents a reference to a parent role
 */
object RoleParent : RoleInfo {
    override val name = "parent";
    override val value = "@parent"
}


/**
 * No roles
 */
object RoleNone : RoleInfo {
    override val name = "none";
    override val value = "@none"
}


/**
 * No roles
 */
data class Role(override val name: String, override val value: String) : RoleInfo

