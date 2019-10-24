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

import slatekit.apis.setup.AuthMode
import slatekit.apis.support.RolesSupport
import slatekit.common.requests.Request
import slatekit.results.Notice
import slatekit.results.Outcome

/**
 * Class used to authenticate an api with support for 3 modes:
 * 1. app-key : user needs to supply an api-key to authenticate
 * 2. app-role: application specific role mode ( will validate against roles )
 * 3. key-role: dual authentication mode that will validate key + role.
 *
 * Need to initialize with api-keys
 */
interface Auth : RolesSupport {

    /**
     * whether or not the authorization is valid for the auth mode and roles supplied.
     * NOTE: This can be implemented any way, e.g.g Auth tokens/OAuth etc.
     *
     * @param authMode : The mode of the authoriation as specified by annotation Api and attribute: auth
     * @param rolesOnAction : The values of the "roles" attribute on the annotation of the ApiAction ( method )
     * @param rolesOnApi : The values of the "roles" attribute on the annotation of the Api ( class )
     * @return
     */
    fun isAuthorized(req: Request, authMode: String, rolesOnAction: String, rolesOnApi: String): Notice<Boolean>

    /**
     * whether or not the authorization is valid for the auth mode and roles supplied.
     * NOTE: This can be implemented any way, e.g.g Auth tokens/OAuth etc.
     *
     * @param authMode : The mode of the authoriation as specified by annotation Api and attribute: auth
     * @param rolesOnAction : The values of the "roles" attribute on the annotation of the ApiAction ( method )
     * @param rolesOnApi : The values of the "roles" attribute on the annotation of the Api ( class )
     * @return
     */
    fun check(req: Request, authMode: AuthMode, rolesOnAction: slatekit.apis.core.Roles, rolesOnApi: slatekit.apis.core.Roles): Outcome<Boolean>


    /**
     * Gets the user roles that are applicable for the supplied request.
     * This can be implemented any way, e.g. Auth tokens/OAuth etc.
     */
    fun getUserRoles(req: Request): String = ""


}



