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

package slatekit.apis.core

import slatekit.apis.ApiConstants
import slatekit.common.Request
import slatekit.common.Result


/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate
 * 2. app-role: application specific role mode ( will validate against roles )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
interface Auth {

    /**
     * whether or not the authorization is valid for the auth mode and roles supplied.
     * NOTE: This can be implemented any way, e.g.g Auth tokens/OAuth etc.
     *
     * @param authMode       : The mode of the authoriation as specified by annotation Api and attribute: auth
     * @param rolesOnAction  : The values of the "roles" attribute on the annotation of the ApiAction ( method )
     * @param rolesOnApi     : The values of the "roles" attribute on the annotation of the Api ( class )
     * @return
     */
    fun isAuthorized(req: Request, authMode: String, rolesOnAction: String, rolesOnApi: String): Result<Boolean>


    /**
     * Gets the user roles that are applicable for the supplied request.
     * This can be implemented any way, e.g. Auth tokens/OAuth etc.
     */
    fun getUserRoles(req: Request): String = ""


    /**
     * Whether the role supplied is a guest role via "?"
     */
    fun isRoleGuest (role:String): Boolean = role == ApiConstants.Unknown


    /**
     * Whether the role supplied is any role via "*"
     */
    fun isRoleAny   (role:String): Boolean = role == ApiConstants.Any


    /**
     * Whether the role supplied is a referent to the parent role via "@parent"
     */
    fun isRoleParent(role:String): Boolean = role == ApiConstants.Parent


    /**
     * Whether the role supplied is an empty role indicating public access.
     */
    fun isRoleEmpty(role:String): Boolean = role == ApiConstants.None


    /**
     * Whether the role is empty "" or a guest role "?"
     */
    fun isRoleEmptyOrGuest(role:String):Boolean = isRoleEmpty(role) || isRoleGuest(role)


    /**
     * gets the primary value supplied unless it references the parent value via "@parent"
     * @param primary
     * @param parent
     * @return
     */
    fun determineRole(primary: String?, parent: String): String {
        return if(primary != null && !primary.isNullOrEmpty() && primary != ApiConstants.Parent)
            primary
        else
            parent
    }
}
