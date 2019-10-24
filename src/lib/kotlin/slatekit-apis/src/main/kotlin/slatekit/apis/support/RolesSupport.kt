package slatekit.apis.support

import slatekit.common.auth.Roles

interface RolesSupport {
    /**
     * Whether the role supplied is a guest role via "?"
     */
    fun isRoleGuest(role: String): Boolean = role == Roles.guest

    /**
     * Whether the role supplied is any role via "*"
     */
    fun isRoleAny(role: String): Boolean = role == Roles.all

    /**
     * Whether the role supplied is a referent to the parent role via "@parent"
     */
    fun isRoleParent(role: String): Boolean = role == Roles.parent

    /**
     * Whether the role supplied is an empty role indicating public access.
     */
    fun isRoleEmpty(role: String): Boolean = role == Roles.none

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
        return if (primary != null && !primary.isNullOrEmpty() && primary != Roles.parent)
            primary
        else
            parent
    }

}