package kiit.apis.support

import kiit.common.auth.Roles

interface RolesSupport {
    /**
     * Whether the role supplied is a guest role via "?"
     */
    fun isRoleGuest(role: String): Boolean = role == Roles.GUEST

    /**
     * Whether the role supplied is any role via "*"
     */
    fun isRoleAny(role: String): Boolean = role == Roles.ALL

    /**
     * Whether the role supplied is a referent to the parent role via "@parent"
     */
    fun isRoleParent(role: String): Boolean = role == Roles.PARENT

    /**
     * Whether the role supplied is an empty role indicating public access.
     */
    fun isRoleEmpty(role: String): Boolean = role == Roles.NONE

    /**
     * Whether the role is empty "" or a guest role "?"
     */
    fun isRoleEmptyOrGuest(role: String): Boolean = isRoleEmpty(role) || isRoleGuest(role)

    /**
     * gets the primary value supplied unless it references the parent value via "@parent"
     * @param primary
     * @param parent
     * @return
     */
    fun determineRole(primary: String?, parent: String): String {
        return if (primary != null && !primary.isNullOrEmpty() && primary != Roles.PARENT)
            primary
        else
            parent
    }
}
