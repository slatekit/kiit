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

package kiit.apis.core

import kiit.apis.AuthMode
import kiit.apis.support.RolesSupport
import kiit.requests.Request
import kiit.results.Notice
import kiit.results.Outcome

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
     * @param roles : The values of the "roles" attribute on the annotation of the Action ( class )
     * @return
     */
    fun isAuthorized(req: Request, authMode: AuthMode, roles:Roles): Outcome<Boolean>

    /**
     * whether or not the authorization is valid for the auth mode and roles supplied.
     * NOTE: This can be implemented any way, e.g.g Auth tokens/OAuth etc.
     *
     * @param authMode : The mode of the authoriation as specified by annotation Api and attribute: auth
     * @param roles : The values of the "roles" attribute on the annotation of the Action ( class )
     * @return
     */
    fun check(req: Request, authMode: AuthMode, roles: Roles): Outcome<Boolean>

    /**
     * Gets the user roles that are applicable for the supplied request.
     * This can be implemented any way, e.g. Auth tokens/OAuth etc.
     */
    fun getUserRoles(req: Request): String = ""
}
